package com.resumeanalyzer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONArray;
import org.json.JSONObject;

public class ResumeAnalyzer extends JFrame {

    private JTextField apiKeyField;
    private JTextArea resumeArea;
    private JTextArea jobDescArea;
    private JTextArea resultArea;
    private JButton uploadBtn;
    private JButton analyzeBtn;
    private JButton saveReportBtn;
    private JLabel statusLabel;
    private JProgressBar progressBar;

    private static final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    public ResumeAnalyzer() {
        setTitle("AI-Powered Resume Analyzer | Gemini AI");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel rootPanel = new JPanel(new BorderLayout(12, 12));
        rootPanel.setBorder(new EmptyBorder(14, 14, 14, 14));

        // Top Header Banner
        JPanel headerWrapper = new JPanel(new BorderLayout(0, 10));

        JPanel titleBanner = new JPanel(new BorderLayout());
        titleBanner.setBackground(new Color(30, 41, 59));
        titleBanner.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel titleLbl = new JLabel("AI-Powered Resume Analyzer");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLbl.setForeground(Color.WHITE);

        JLabel subtitleLbl = new JLabel("Skill Gap Detection | Match Score | Interview Prep");
        subtitleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitleLbl.setForeground(new Color(203, 213, 225));

        titleBanner.add(titleLbl, BorderLayout.WEST);
        titleBanner.add(subtitleLbl, BorderLayout.EAST);

        // API Key and Upload Bar
        JPanel controlsBar = new JPanel(new BorderLayout(10, 5));
        JPanel apiPanel = new JPanel(new BorderLayout(8, 5));
        JLabel keyLbl = new JLabel("Gemini API Key:");
        keyLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        apiPanel.add(keyLbl, BorderLayout.WEST);

        apiKeyField = new JPasswordField();
        apiKeyField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        apiPanel.add(apiKeyField, BorderLayout.CENTER);

        uploadBtn = new JButton("Upload Resume (PDF)");
        uploadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        uploadBtn.addActionListener(e -> uploadPdfResume());

        controlsBar.add(apiPanel, BorderLayout.CENTER);
        controlsBar.add(uploadBtn, BorderLayout.EAST);

        headerWrapper.add(titleBanner, BorderLayout.NORTH);
        headerWrapper.add(controlsBar, BorderLayout.CENTER);

        // Center Split Pane: Inputs on Left, AI Report on Right
        JPanel inputPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        resumeArea = createTextArea(true);
        JScrollPane resumeScroll = new JScrollPane(resumeArea);
        resumeScroll.setBorder(BorderFactory.createTitledBorder("1. Resume Text (Upload PDF or Paste Here)"));

        jobDescArea = createTextArea(true);
        JScrollPane jobScroll = new JScrollPane(jobDescArea);
        jobScroll.setBorder(BorderFactory.createTitledBorder("2. Target Job Description"));

        inputPanel.add(resumeScroll);
        inputPanel.add(jobScroll);

        resultArea = createTextArea(false);
        resultArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane resultScroll = new JScrollPane(resultArea);
        resultScroll.setBorder(BorderFactory.createTitledBorder("3. AI Analysis Report, Match Score & Interview Prep"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, resultScroll);
        splitPane.setDividerLocation(490);

        // Bottom Bar: Status, Progress Bar & Action Buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(12, 5));

        JPanel statusPanel = new JPanel(new BorderLayout(8, 4));
        statusLabel = new JLabel("Ready. Paste your Gemini API key, add resume & job description, then click Analyze.");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);

        statusPanel.add(statusLabel, BorderLayout.CENTER);
        statusPanel.add(progressBar, BorderLayout.SOUTH);

        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));

        saveReportBtn = new JButton("Save Report (.txt)");
        saveReportBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveReportBtn.setEnabled(false);
        saveReportBtn.addActionListener(e -> saveReportToFile());

        analyzeBtn = new JButton("Analyze Resume with Gemini AI");
        analyzeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        analyzeBtn.addActionListener(e -> runAnalysis());

        buttonGroup.add(saveReportBtn);
        buttonGroup.add(analyzeBtn);

        bottomPanel.add(statusPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonGroup, BorderLayout.EAST);

        rootPanel.add(headerWrapper, BorderLayout.NORTH);
        rootPanel.add(splitPane, BorderLayout.CENTER);
        rootPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(rootPanel);
    }

    private JTextArea createTextArea(boolean editable) {
        JTextArea area = new JTextArea();
        area.setEditable(editable);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        area.setMargin(new Insets(6, 8, 6, 8));
        return area;
    }

    private void uploadPdfResume() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File pdfFile = fileChooser.getSelectedFile();
            try (PDDocument document = Loader.loadPDF(pdfFile)) {
                PDFTextStripper stripper = new PDFTextStripper();
                resumeArea.setText(stripper.getText(document));
                statusLabel.setText("Loaded PDF: " + pdfFile.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error reading PDF: " + ex.getMessage(), "PDF Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveReportToFile() {
        String content = resultArea.getText().trim();
        if (content.isEmpty()) {
            return;
        }
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Resume Analysis Report");
        fileChooser.setSelectedFile(new File("Resume_Analysis_Report.txt"));
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            if (!fileToSave.getName().toLowerCase().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write("=== AI-POWERED RESUME ANALYSIS REPORT ===\n\n");
                writer.write(content);
                statusLabel.setText("Report saved to: " + fileToSave.getName());
                JOptionPane.showMessageDialog(this, "Report saved successfully!", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void runAnalysis() {
        String apiKey = apiKeyField.getText().trim();
        String resumeText = resumeArea.getText().trim();
        String jobDesc = jobDescArea.getText().trim();

        if (apiKey.isEmpty() || resumeText.isEmpty() || jobDesc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your Gemini API Key, Resume text, and Job Description.", "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        analyzeBtn.setEnabled(false);
        saveReportBtn.setEnabled(false);
        progressBar.setVisible(true);
        statusLabel.setText("Analyzing resume with Gemini AI... Please wait.");
        resultArea.setText("Generating AI analysis...");

        SwingWorker<String, Void> worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                return callGeminiApi(apiKey, resumeText, jobDesc);
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                try {
                    resultArea.setText(get());
                    resultArea.setCaretPosition(0);
                    statusLabel.setText("Analysis complete! You can now click 'Save Report (.txt)'.");
                    saveReportBtn.setEnabled(true);
                } catch (Exception ex) {
                    resultArea.setText("Error calling Gemini API:\n" + ex.getMessage());
                    statusLabel.setText("Error occurred during analysis.");
                } finally {
                    analyzeBtn.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private String callGeminiApi(String apiKey, String resumeText, String jobDesc) throws Exception {
        if (resumeText.length() > 4000) {
            resumeText = resumeText.substring(0, 4000);
        }
        if (jobDesc.length() > 3000) {
            jobDesc = jobDesc.substring(0, 3000);
        }

        String prompt = "You are an expert technical recruiter. Analyze this resume against the job description concisely.\n\n"
                + "Provide these 5 short sections:\n"
                + "1. ESTIMATED MATCH SCORE: (Percentage 0%-100% + 2-sentence summary)\n"
                + "2. MATCHING SKILLS: (Short bullet list)\n"
                + "3. MISSING SKILLS & GAPS: (Short bullet list)\n"
                + "4. RESUME IMPROVEMENT SUGGESTIONS: (3-4 actionable bullet points)\n"
                + "5. INTERVIEW PREPARATION RECOMMENDATIONS: (3 likely interview questions + brief answer tips)\n\n"
                + "--- RESUME ---\n" + resumeText + "\n\n"
                + "--- JOB DESCRIPTION ---\n" + jobDesc;

        JSONObject textPart = new JSONObject().put("text", prompt);
        JSONObject contentObj = new JSONObject().put("parts", new JSONArray().put(textPart));
        JSONObject generationConfig = new JSONObject().put("maxOutputTokens", 1200);

        JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray().put(contentObj))
                .put("generationConfig", generationConfig);

        String[] models = {"gemini-3.5-flash-lite", "gemini-3.5-flash", "gemini-3.8-flash"};
        String lastError = "";

        for (int i = 0; i < models.length; i++) {
            String url = "https://" + "generativelanguage." + "googleapis.com"
                       + "/v1beta/models/" + models[i] + ":generateContent";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(60))
                    .header("Content-Type", "application/json")
                    .header("x-goog-api-key", apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JSONObject jsonResponse = new JSONObject(response.body());
                if (jsonResponse.has("candidates")) {
                    JSONArray candidates = jsonResponse.getJSONArray("candidates");
                    if (candidates.length() > 0 && candidates.getJSONObject(0).has("content")) {
                        return candidates.getJSONObject(0)
                                .getJSONObject("content")
                                .getJSONArray("parts")
                                .getJSONObject(0)
                                .getString("text");
                    }
                }
                lastError = "Unexpected API response: " + response.body();
                continue;
            }

            lastError = "HTTP " + response.statusCode() + ": " + response.body();
            if (response.statusCode() == 503) {
                Thread.sleep(2000);
            } else if (response.statusCode() != 404) {
                break;
            }
        }
        throw new RuntimeException(lastError);
    }
        public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        SwingUtilities.invokeLater(() -> {
        try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        new ResumeAnalyzer().setVisible(true);
        });
        }
        }