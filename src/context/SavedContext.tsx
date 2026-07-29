import React, { createContext, useContext, useEffect, useState } from 'react';
import { SavedItem } from '../types';
import { getSavedItems, saveItemToFirestore, removeSavedItemFromFirestore } from '../services/db';

interface SavedContextType {
  savedItems: SavedItem[];
  isSaved: (itemId: string) => boolean;
  toggleSave: (item: Omit<SavedItem, 'id' | 'savedAt'>) => Promise<void>;
  removeSaved: (itemId: string) => Promise<void>;
}

const SavedContext = createContext<SavedContextType | undefined>(undefined);

const LOCAL_SAVED_KEY = 'towfik_saved_items_v1';

export const SavedProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [savedItems, setSavedItems] = useState<SavedItem[]>(() => {
    try {
      const local = localStorage.getItem(LOCAL_SAVED_KEY);
      return local ? JSON.parse(local) : [];
    } catch {
      return [];
    }
  });

  useEffect(() => {
    // Sync with Firestore on mount
    getSavedItems().then(remoteItems => {
      if (remoteItems.length > 0) {
        setSavedItems(remoteItems);
        localStorage.setItem(LOCAL_SAVED_KEY, JSON.stringify(remoteItems));
      }
    }).catch(err => console.log('Saved items fetch error:', err));
  }, []);

  const saveToLocal = (items: SavedItem[]) => {
    setSavedItems(items);
    localStorage.setItem(LOCAL_SAVED_KEY, JSON.stringify(items));
  };

  const isSaved = (itemId: string): boolean => {
    return savedItems.some(item => item.itemId === itemId);
  };

  const toggleSave = async (itemData: Omit<SavedItem, 'id' | 'savedAt'>) => {
    const existing = savedItems.find(i => i.itemId === itemData.itemId);
    if (existing) {
      await removeSaved(itemData.itemId);
    } else {
      const newItem: SavedItem = {
        ...itemData,
        id: `local_${Date.now()}_${Math.random().toString(36).substring(2, 6)}`,
        savedAt: new Date().toISOString()
      };

      const updated = [newItem, ...savedItems];
      saveToLocal(updated);

      try {
        const firestoreId = await saveItemToFirestore({
          ...itemData,
          savedAt: newItem.savedAt
        });
        // Update local item with true firestore ID
        const synced = updated.map(u => u.itemId === newItem.itemId ? { ...u, id: firestoreId } : u);
        saveToLocal(synced);
      } catch (e) {
        console.error('Firestore save failed, stored locally:', e);
      }
    }
  };

  const removeSaved = async (itemId: string) => {
    const itemToRemove = savedItems.find(i => i.itemId === itemId);
    const updated = savedItems.filter(i => i.itemId !== itemId);
    saveToLocal(updated);

    if (itemToRemove && itemToRemove.id && !itemToRemove.id.startsWith('local_')) {
      try {
        await removeSavedItemFromFirestore(itemToRemove.id);
      } catch (e) {
        console.error('Firestore remove saved error:', e);
      }
    }
  };

  return (
    <SavedContext.Provider value={{ savedItems, isSaved, toggleSave, removeSaved }}>
      {children}
    </SavedContext.Provider>
  );
};

export const useSaved = () => {
  const ctx = useContext(SavedContext);
  if (!ctx) throw new Error('useSaved must be used within SavedProvider');
  return ctx;
};
