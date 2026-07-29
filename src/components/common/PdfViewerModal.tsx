import React from 'react';
import { X, Download, Printer, FileText, Check, ExternalLink } from 'lucide-react';
import { downloadPdfFile, openPdfPrintWindow } from '../../utils/pdfGenerator';

interface PdfViewerModalProps {
  isOpen: boolean;
  title: string;
  subtitle?: string;
  htmlContent?: string;
  pdfUrl?: string;
  onClose: () => void;
}

export const PdfViewerModal: React.FC<PdfViewerModalProps> = ({
  isOpen,
  title,
  subtitle,
  htmlContent,
  pdfUrl,
  onClose
}) => {
  if (!isOpen) return null;

  const handleDownload = () => {
    if (pdfUrl && pdfUrl.startsWith('http')) {
      window.open(pdfUrl, '_blank');
    } else if (htmlContent) {
      downloadPdfFile(htmlContent, `${title.replace(/[^a-zA-Z0-9\u0980-\u09FF]/g, '_')}_Study_Material.pdf`);
    }
  };

  const handlePrint = () => {
    if (htmlContent) {
      openPdfPrintWindow(htmlContent);
    } else if (pdfUrl) {
      window.open(pdfUrl, '_blank');
    }
  };

  return (
    <div className="fixed inset-0 z-50 bg-slate-950/80 backdrop-blur-sm flex items-center justify-center p-3 sm:p-6 animate-in fade-in duration-200">
      <div className="w-full max-w-2xl bg-white dark:bg-slate-900 rounded-3xl border border-slate-200 dark:border-slate-800 shadow-2xl flex flex-col max-h-[90vh] overflow-hidden">
        {/* Modal Header */}
        <div className="p-4 bg-slate-900 text-white flex items-center justify-between gap-3 shrink-0">
          <div className="flex items-center gap-2.5 min-w-0">
            <div className="w-9 h-9 rounded-xl bg-rose-600 text-white flex items-center justify-center shrink-0">
              <FileText size={20} />
            </div>
            <div className="min-w-0">
              <h3 className="font-extrabold text-sm truncate">{title}</h3>
              {subtitle && <p className="text-[10px] text-slate-400 truncate">{subtitle}</p>}
            </div>
          </div>

          <div className="flex items-center gap-1.5 shrink-0">
            <button
              onClick={handlePrint}
              title="Print / Save PDF"
              className="p-2 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-200 transition text-xs font-bold flex items-center gap-1"
            >
              <Printer size={16} />
              <span className="hidden sm:inline">Print</span>
            </button>

            <button
              onClick={handleDownload}
              title="Download PDF File"
              className="px-3 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-700 text-white transition text-xs font-bold flex items-center gap-1 shadow"
            >
              <Download size={16} />
              <span>Download</span>
            </button>

            <button
              onClick={onClose}
              className="p-2 rounded-xl text-slate-400 hover:text-white hover:bg-slate-800 transition"
            >
              <X size={20} />
            </button>
          </div>
        </div>

        {/* Modal Content Body */}
        <div className="flex-1 overflow-y-auto p-4 sm:p-6 bg-slate-50 dark:bg-slate-950">
          {pdfUrl && !htmlContent ? (
            <div className="flex flex-col items-center justify-center py-12 text-center space-y-4">
              <div className="w-16 h-16 rounded-2xl bg-rose-100 dark:bg-rose-950/60 text-rose-600 flex items-center justify-center">
                <FileText size={32} />
              </div>
              <div>
                <h4 className="font-extrabold text-base text-slate-900 dark:text-slate-100">{title}</h4>
                <p className="text-xs text-slate-500 max-w-sm mt-1">
                  Direct download link to official WBBSE Madhyamik chapter PDF document.
                </p>
              </div>
              <a
                href={pdfUrl}
                target="_blank"
                rel="noreferrer"
                className="px-5 py-2.5 bg-rose-600 hover:bg-rose-700 text-white rounded-xl font-bold text-xs flex items-center gap-2 shadow-md transition"
              >
                <span>Open PDF Document</span>
                <ExternalLink size={16} />
              </a>
            </div>
          ) : htmlContent ? (
            <div className="bg-white text-slate-900 p-6 rounded-2xl shadow-sm border border-slate-200 font-sans">
              <iframe
                srcDoc={htmlContent}
                title={title}
                className="w-full h-[60vh] border-0 rounded-xl"
              />
            </div>
          ) : (
            <div className="text-center py-8 text-xs text-slate-500">
              No content preview available for this PDF link.
            </div>
          )}
        </div>

        {/* Modal Footer */}
        <div className="p-3 bg-white dark:bg-slate-900 border-t border-slate-200 dark:border-slate-800 text-[11px] text-slate-500 flex items-center justify-between shrink-0">
          <span>Official Towfik Edutips Printable PDF Document</span>
          <button
            onClick={onClose}
            className="px-3 py-1 bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 text-slate-700 dark:text-slate-300 rounded-lg font-bold"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};
