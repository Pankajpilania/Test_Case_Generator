# Test Case Generator (Spring Boot + React)

A full-stack web application that generates QA test cases from a user story.

## Tech Stack
- **Backend:** Java 17, Spring Boot 3
- **Frontend:** React + Vite
- **Exports:** CSV and Excel (.xlsx)

## Features
- Paste a user story
- Select test case count: 5 / 10 / 20 / 40
- Select test types: Positive / Negative / Edge
- Generate realistic test cases in table format
- Download generated cases as CSV and XLSX

## Project Structure

```text
Test_Case_Generator/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/example/testcasegenerator/
│       │   ├── TestCaseGeneratorApplication.java
│       │   ├── controller/TestCaseController.java
│       │   ├── dto/
│       │   │   ├── GenerateTestCaseRequest.java
│       │   │   └── GenerateTestCaseResponse.java
│       │   ├── exception/GlobalExceptionHandler.java
│       │   ├── model/
│       │   │   ├── TestCase.java
│       │   │   └── TestCaseType.java
│       │   └── service/
│       │       ├── FileExportService.java
│       │       └── TestCaseGenerationService.java
│       └── resources/application.properties
├── frontend/
│   ├── package.json
│   ├── vite.config.js
│   ├── index.html
│   └── src/
│       ├── App.jsx
│       ├── main.jsx
│       ├── styles.css
│       ├── components/TestCaseTable.jsx
│       └── services/api.js
└── README.md
```

## Prerequisites
- Java **17+**
- Maven **3.9+**
- Node.js **20+**
- npm **10+**

## Run Locally

### 1) Start Backend
```bash
cd backend
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`.

### 2) Start Frontend
Open a new terminal:
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:5173`.

## API Contract

### `POST /api/generate-testcases`

Request:
```json
{
  "userStory": "As a customer, I want to reset my password so that I can regain account access.",
  "numberOfCases": 10,
  "types": ["POSITIVE", "NEGATIVE", "EDGE"]
}
```

Response:
```json
{
  "userStory": "...",
  "generatedCount": 10,
  "testCases": [
    {
      "testCaseId": "TC-001",
      "title": "...",
      "description": "...",
      "preconditions": "...",
      "steps": ["..."],
      "expectedResult": "...",
      "type": "POSITIVE"
    }
  ]
}
```

### Export Endpoints
- `POST /api/generate-testcases/export/csv`
- `POST /api/generate-testcases/export/xlsx`

Both endpoints accept the same JSON payload and return downloadable files.

## Notes
- The UI includes a placeholder area where you can add your image/branding later.
- CORS is configured for local frontend development at `http://localhost:5173`.
