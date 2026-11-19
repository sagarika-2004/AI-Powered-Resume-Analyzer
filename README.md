# AI-Powered Resume Analyzer (Java Swing + Gemini AI)

A desktop application built with **Java 17**, **Java Swing**, **Apache PDFBox**, and **Google Gemini AI API** that evaluates PDF or plain-text resumes against target job descriptions to provide match scores, skill gap insights, resume improvement tips, and tailored interview questions.

## Key Features
- **PDF Resume Extraction:** Automatically parses text from uploaded `.pdf` resumes using Apache PDFBox.
- **AI-Powered Match Scoring:** Generates an estimated match percentage and summary comparing the resume with the target job description.
- **Skill Gap & Improvement Analysis:** Identifies matching skills, missing keywords/skills, and actionable suggestions to tailor the resume.
- **Interview Preparation:** Suggests likely role-specific interview questions along with brief answering tips.
- **Responsive UI & Multi-Model Fallback:** Uses `SwingWorker` for non-blocking background API calls, HTTP timeout protection, and automatic model fallback across Gemini Flash models.
- **Exportable Reports:** Allows users to save the complete AI evaluation report as a `.txt` file.

## Tech Stack
- **Language:** Java 17
- **GUI Framework:** Java Swing
- **Build & Dependency Management:** Apache Maven
- **Libraries:** Apache PDFBox (`3.0.3`), `org.json` (`20240303`), Java `java.net.http.HttpClient`
- **AI Integration:** Google Gemini REST API

## How to Run Locally
1. Clone this repository:
   ```bash
   git clone https://github.com
   ```
2. Open the project as an **Existing Maven Project** in Eclipse or IntelliJ IDEA.
3. Ensure **Java 17+** is configured and let Maven download `pdfbox` and `org.json`.
4. Run `ResumeAnalyzer.java`.
5. Enter your **Google Gemini API Key** (from [Google AI Studio](https://google.com)), upload a PDF resume, paste a job description, and click **Analyze Resume with Gemini AI**.
