import React, { useState, useEffect } from 'react';
import { Banner } from '../../types';
import { ChevronLeft, ChevronRight } from 'lucide-react';

interface BannerSliderProps {
  banners: Banner[];
  onSelectBanner?: (banner: Banner) => void;
}

export const BannerSlider: React.FC<BannerSliderProps> = ({ banners, onSelectBanner }) => {
  const [currentIndex, setCurrentIndex] = useState(0);

  useEffect(() => {
    if (banners.length <= 1) return;
    const interval = setInterval(() => {
      setCurrentIndex(prev => (prev + 1) % banners.length);
    }, 4500);
    return () => clearInterval(interval);
  }, [banners.length]);

  if (banners.length === 0) return null;

  const current = banners[currentIndex];

  const handlePrev = (e: React.MouseEvent) => {
    e.stopPropagation();
    setCurrentIndex(prev => (prev - 1 + banners.length) % banners.length);
  };

  const handleNext = (e: React.MouseEvent) => {
    e.stopPropagation();
    setCurrentIndex(prev => (prev + 1) % banners.length);
  };

  return (
    <div className="relative w-full rounded-2xl overflow-hidden shadow-md bg-slate-100 dark:bg-slate-800 aspect-[16/8]">
      <div
        className="w-full h-full cursor-pointer relative group"
        onClick={() => onSelectBanner && onSelectBanner(current)}
      >
        <img
          src={current.imageUrl}
          alt={current.title}
          className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
          referrerPolicy="no-referrer"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-950/80 via-slate-950/20 to-transparent flex flex-col justify-end p-4">
          <span className="inline-block px-2 py-0.5 mb-1 text-[10px] font-extrabold uppercase bg-emerald-500 text-white rounded-md self-start">
            Featured Notice
          </span>
          <h3 className="text-white text-sm md:text-base font-bold leading-snug drop-shadow-sm">
            {current.title}
          </h3>
        </div>
      </div>

      {banners.length > 1 && (
        <>
          <button
            onClick={handlePrev}
            className="absolute left-2 top-1/2 -translate-y-1/2 w-8 h-8 rounded-full bg-black/40 hover:bg-black/60 text-white flex items-center justify-center backdrop-blur-sm transition active:scale-95"
          >
            <ChevronLeft size={18} />
          </button>
          <button
            onClick={handleNext}
            className="absolute right-2 top-1/2 -translate-y-1/2 w-8 h-8 rounded-full bg-black/40 hover:bg-black/60 text-white flex items-center justify-center backdrop-blur-sm transition active:scale-95"
          >
            <ChevronRight size={18} />
          </button>

          {/* Indicators */}
          <div className="absolute bottom-2 right-4 flex items-center gap-1.5 z-10">
            {banners.map((_, idx) => (
              <button
                key={idx}
                onClick={(e) => {
                  e.stopPropagation();
                  setCurrentIndex(idx);
                }}
                className={`h-1.5 rounded-full transition-all duration-300 ${
                  idx === currentIndex ? 'w-5 bg-emerald-400' : 'w-1.5 bg-white/50'
                }`}
                aria-label={`Go to slide ${idx + 1}`}
              />
            ))}
          </div>
        </>
      )}
    </div>
  );
};
