import { Chapter, Note, Question, Subject } from '../types';

/**
 * Generates formatted HTML string for printable/downloadable Chapter PDF document.
 * Excludes MCQs and groups all short/long/suggestion/PYQ questions serially by mark division.
 */
export function generateChapterPdfHtml(
  chapter: Chapter,
  notes: Note[],
  questions: Question[],
  appName: string = 'Towfik Edutips'
): string {
  // Exclude MCQs as requested (without MCQ)
  const nonMcqQuestions = questions.filter(q => q.category !== 'mcq');

  // Group questions by mark weightage (1 Mark, 2 Marks, 3 Marks, 5 Marks, etc.)
  const marksMap: { [mark: number]: Question[] } = {};
  nonMcqQuestions.forEach(q => {
    const mark = q.marks || 1;
    if (!marksMap[mark]) marksMap[mark] = [];
    marksMap[mark].push(q);
  });

  // Sort mark divisions in ascending order (1, 2, 3, 5...)
  const sortedMarkDivisions = Object.keys(marksMap)
    .map(Number)
    .sort((a, b) => a - b);

  let globalQuestionCounter = 1;

  return `
<!DOCTYPE html>
<html lang="bn">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${chapter.chapterName} - Full Chapter PDF (${appName})</title>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Hind+Siliguri:wght@400;600;700&display=swap');
    body {
      font-family: 'Hind Siliguri', 'Segoe UI', Tahoma, Geneva, sans-serif;
      line-height: 1.6;
      color: #0f172a;
      margin: 0;
      padding: 30px;
      background: #ffffff;
    }
    .header {
      text-align: center;
      padding-bottom: 18px;
      border-bottom: 3px double #059669;
      margin-bottom: 22px;
    }
    .header h1 {
      margin: 0;
      color: #059669;
      font-size: 26px;
      font-weight: 700;
    }
    .header p {
      margin: 4px 0 0 0;
      color: #64748b;
      font-size: 13px;
      font-weight: 600;
    }
    .meta-box {
      background: #ecfdf5;
      border: 1.5px solid #6ee7b7;
      padding: 14px 20px;
      border-radius: 12px;
      margin-bottom: 25px;
    }
    .meta-box h2 {
      margin: 0 0 4px 0;
      color: #047857;
      font-size: 20px;
    }
    .meta-box p {
      margin: 0;
      color: #065f46;
      font-size: 13px;
    }
    .section-title {
      font-size: 16px;
      font-weight: 700;
      color: #0f172a;
      background: #f1f5f9;
      border-left: 5px solid #059669;
      padding: 8px 12px;
      margin-top: 25px;
      margin-bottom: 15px;
      border-radius: 0 8px 8px 0;
    }
    .card {
      background: #ffffff;
      border: 1px solid #cbd5e1;
      border-radius: 10px;
      padding: 14px 18px;
      margin-bottom: 14px;
      page-break-inside: avoid;
      box-shadow: 0 1px 3px rgba(0,0,0,0.04);
    }
    .card-title {
      font-weight: 700;
      font-size: 15px;
      color: #0f172a;
      margin-bottom: 6px;
    }
    .card-content {
      font-size: 14px;
      color: #334155;
      white-space: pre-line;
    }
    .answer-box {
      background: #f8fafc;
      border-left: 3px solid #10b981;
      padding: 10px 14px;
      margin-top: 8px;
      border-radius: 6px;
      font-size: 14px;
      color: #1e293b;
    }
    .badge {
      display: inline-block;
      padding: 2px 8px;
      font-size: 11px;
      font-weight: 700;
      border-radius: 4px;
      background: #d1fae5;
      color: #065f46;
      margin-bottom: 6px;
    }
    .badge-suggestion {
      background: #fef3c7;
      color: #92400e;
    }
    .badge-pyq {
      background: #e0e7ff;
      color: #3730a3;
    }
    .footer {
      text-align: center;
      margin-top: 40px;
      padding-top: 15px;
      border-top: 1px solid #e2e8f0;
      font-size: 12px;
      color: #94a3b8;
    }
    @media print {
      body { padding: 0; }
    }
  </style>
</head>
<body>
  <div class="header">
    <h1>${appName} — WBBSE Madhyamik Board Study Portal</h1>
    <p>Official Complete Chapter Solved Study Notes & Question Bank PDF</p>
  </div>

  <div class="meta-box">
    <h2>${chapter.chapterName}</h2>
    <p>Subject: <strong>${chapter.subjectName || 'WBBSE Syllabus'}</strong> ${chapter.description ? `| ${chapter.description}` : ''} | Total Solved Questions: ${nonMcqQuestions.length}</p>
  </div>

  ${notes.length > 0 ? `
    <div class="section-title">📖 Section A: Chapter Summaries & Important Notes</div>
    ${notes.map(n => `
      <div class="card">
        <div class="card-title">${n.title}</div>
        <div class="card-content">${n.content}</div>
      </div>
    `).join('')}
  ` : ''}

  ${sortedMarkDivisions.length > 0 ? sortedMarkDivisions.map(mark => {
    const markQuestions = marksMap[mark];
    let sectionLabel = `${mark} Mark ${mark === 1 ? 'Very Short Question' : mark <= 3 ? 'Short Question' : 'Long Question / Essay'}`;
    if (mark >= 5) sectionLabel = `${mark} Marks Madhyamik High-Yield & Suggestions`;

    return `
      <div class="section-title">📝 Mark Division: ${mark} ${mark === 1 ? 'Mark' : 'Marks'} Questions (${sectionLabel})</div>
      ${markQuestions.map(q => {
        const serialNum = globalQuestionCounter++;
        const isSuggestion = q.category === 'madhyamik_suggestion';
        const isPyq = q.category === 'pyq';

        return `
          <div class="card">
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span class="badge ${isSuggestion ? 'badge-suggestion' : isPyq ? 'badge-pyq' : ''}">
                ${isSuggestion ? '🏆 Madhyamik Suggestion' : isPyq ? `📜 PYQ ${q.year || ''}` : `${q.category.toUpperCase()}`} • ${mark} ${mark === 1 ? 'Mark' : 'Marks'}
              </span>
              <span style="font-size: 11px; font-weight: bold; color: #64748b;">Serial Q${serialNum}</span>
            </div>
            <div class="card-title">Q${serialNum}. ${q.questionText}</div>
            <div class="answer-box">
              <strong>Answer / Solution:</strong><br/>
              ${q.answerText || 'See full explanation in portal.'}
            </div>
          </div>
        `;
      }).join('')}
    `;
  }).join('') : `
    <div class="card" style="text-align: center; color: #64748b;">
      No non-MCQ questions added for this chapter yet.
    </div>
  `}

  <div class="footer">
    <p>Generated by <strong>${appName}</strong> • WBBSE Madhyamik Exam Preparation Portal</p>
  </div>
</body>
</html>
  `;
}

/**
 * Generates formatted HTML string for Madhyamik Suggestions PDF grouped mark-wise serially
 */
export function generateSuggestionsPdfHtml(
  questions: Question[],
  chapters: Chapter[],
  subjects: Subject[],
  appName: string = 'Towfik Edutips'
): string {
  // Exclude MCQs
  const nonMcqQuestions = questions.filter(q => q.category !== 'mcq');

  // Group by marks ascending
  const marksMap: { [mark: number]: Question[] } = {};
  nonMcqQuestions.forEach(q => {
    const mark = q.marks || 5;
    if (!marksMap[mark]) marksMap[mark] = [];
    marksMap[mark].push(q);
  });

  const sortedMarkDivisions = Object.keys(marksMap)
    .map(Number)
    .sort((a, b) => a - b);

  let globalQuestionCounter = 1;

  return `
<!DOCTYPE html>
<html lang="bn">
<head>
  <meta charset="UTF-8">
  <title>Madhyamik 2026 Suggestions - ${appName}</title>
  <style>
    @import url('https://fonts.googleapis.com/css2?family=Hind+Siliguri:wght@400;600;700&display=swap');
    body {
      font-family: 'Hind Siliguri', sans-serif;
      padding: 30px;
      color: #0f172a;
      background: #fff;
    }
    .header { text-align: center; border-bottom: 3px solid #d97706; padding-bottom: 15px; margin-bottom: 22px; }
    .header h1 { margin: 0; color: #d97706; font-size: 25px; }
    .header p { margin: 4px 0 0; color: #64748b; font-size: 13px; }
    .section-title {
      font-size: 15px;
      font-weight: 700;
      color: #78350f;
      background: #fffbeb;
      border-left: 5px solid #d97706;
      padding: 8px 12px;
      margin-top: 25px;
      margin-bottom: 15px;
      border-radius: 0 8px 8px 0;
    }
    .card { background: #ffffff; border: 1px solid #fde68a; padding: 14px 18px; margin-bottom: 14px; border-radius: 10px; page-break-inside: avoid; }
    .title { font-weight: 700; font-size: 15px; color: #0f172a; margin-bottom: 6px; }
    .ans { background: #fffbeb; border-left: 3px solid #d97706; padding: 10px 14px; margin-top: 8px; border-radius: 6px; font-size: 14px; }
    .badge { display: inline-block; background: #fef3c7; color: #92400e; font-size: 11px; font-weight: bold; padding: 2px 8px; border-radius: 4px; margin-bottom: 6px; }
    .footer { text-align: center; margin-top: 40px; border-top: 1px solid #e2e8f0; padding-top: 15px; font-size: 12px; color: #94a3b8; }
  </style>
</head>
<body>
  <div class="header">
    <h1>🏆 WBBSE Madhyamik 2026 Board Suggestions</h1>
    <p>Serial Wise & Mark Division Wise Solved Exam Predictions (${appName})</p>
  </div>

  ${sortedMarkDivisions.length > 0 ? sortedMarkDivisions.map(mark => {
    const list = marksMap[mark];
    return `
      <div class="section-title">📌 Mark Division: ${mark} ${mark === 1 ? 'Mark' : 'Marks'} Suggestions</div>
      ${list.map(q => {
        const serialNum = globalQuestionCounter++;
        const ch = chapters.find(c => c.id === q.chapterId);
        return `
          <div class="card">
            <div style="display: flex; justify-content: space-between; align-items: center;">
              <span class="badge">Suggestion Q${serialNum} ${ch ? `• ${ch.subjectName}` : ''} • ${mark} Marks</span>
            </div>
            <div class="title">Q${serialNum}. ${q.questionText}</div>
            <div class="ans">
              <strong>Answer / Complete Solution:</strong><br/>
              ${q.answerText || 'See full solution.'}
            </div>
          </div>
        `;
      }).join('')}
    `;
  }).join('') : `
    <div style="text-center: center; color: #64748b;">No suggestions added yet.</div>
  `}

  <div class="footer">
    <p>© 2026 ${appName}. All rights reserved.</p>
  </div>
</body>
</html>
  `;
}

/**
 * Trigger immediate print/download of an HTML-formatted PDF file in browser
 */
export function downloadPdfFile(htmlContent: string, fileName: string) {
  const fullHtml = `
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>${fileName}</title>
</head>
<body>
  ${htmlContent}
  <script>
    window.onload = function() {
      setTimeout(function() {
        window.print();
      }, 400);
    };
  </script>
</body>
</html>
  `;

  const blob = new Blob([fullHtml], { type: 'text/html;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  
  // Try opening printable view window directly
  const printWindow = window.open(url, '_blank');
  if (!printWindow) {
    // If popups blocked, trigger file download fallback
    const a = document.createElement('a');
    a.href = url;
    a.download = fileName.endsWith('.html') || fileName.endsWith('.pdf') ? fileName : `${fileName}.pdf.html`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
}

/**
 * Open HTML content in printable print window or new browser tab
 */
export function openPdfPrintWindow(htmlContent: string) {
  const printWindow = window.open('', '_blank');
  if (printWindow) {
    printWindow.document.write(htmlContent);
    printWindow.document.close();
    printWindow.focus();
    setTimeout(() => {
      printWindow.print();
    }, 500);
  }
}
