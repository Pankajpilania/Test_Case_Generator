import React, { useMemo, useState } from 'react';
import TestCaseTable from './components/TestCaseTable';
import { exportFile, generateTestCases } from './services/api';

const COUNT_OPTIONS = [5, 10, 20, 40];
const TYPE_OPTIONS = [
  { value: 'POSITIVE', label: 'Positive' },
  { value: 'NEGATIVE', label: 'Negative' },
  { value: 'EDGE', label: 'Edge cases' }
];

function App() {
  const [userStory, setUserStory] = useState('');
  const [numberOfCases, setNumberOfCases] = useState(10);
  const [types, setTypes] = useState(['POSITIVE', 'NEGATIVE', 'EDGE']);
  const [testCases, setTestCases] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');

  const payload = useMemo(() => ({ userStory, numberOfCases, types }), [userStory, numberOfCases, types]);

  const onTypeChange = (value) => {
    setTypes((previous) => {
      if (previous.includes(value)) {
        if (previous.length === 1) {
          return previous;
        }
        return previous.filter((item) => item !== value);
      }
      return [...previous, value];
    });
  };

  const onGenerate = async () => {
    setError('');
    if (!userStory.trim()) {
      setError('Please provide a user story before generating test cases.');
      return;
    }

    setIsLoading(true);
    try {
      const data = await generateTestCases(payload);
      setTestCases(data.testCases || []);
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to generate test cases.');
    } finally {
      setIsLoading(false);
    }
  };

  const onExport = async (format) => {
    setError('');
    if (!userStory.trim()) {
      setError('Please provide a user story before export.');
      return;
    }

    try {
      await exportFile(payload, format);
    } catch {
      setError(`Failed to export ${format.toUpperCase()} file.`);
    }
  };

  return (
    <div className="app-shell">
      <header className="app-header">
        <div className="brand">⚗️ Test Case Generator</div>
      </header>

      <main className="container">
        <section className="card">
          <h1>User Story</h1>
          <p className="subtitle">Paste your user story and configure generation options.</p>

          <label htmlFor="userStory" className="field-label">User Story</label>
          <textarea
            id="userStory"
            value={userStory}
            onChange={(e) => setUserStory(e.target.value)}
            placeholder="As a [user], I want to [action] so that [benefit]..."
            rows={6}
          />

          <div className="placeholder-banner">
            <span>🖼️ Image placeholder: you can add your brand illustration here later.</span>
          </div>

          <div className="form-grid">
            <div>
              <p className="field-label">Test Case Types</p>
              <div className="checkbox-group">
                {TYPE_OPTIONS.map((option) => (
                  <label key={option.value}>
                    <input
                      type="checkbox"
                      checked={types.includes(option.value)}
                      onChange={() => onTypeChange(option.value)}
                    />
                    {option.label}
                  </label>
                ))}
              </div>
            </div>

            <div>
              <label htmlFor="count" className="field-label">Count</label>
              <select
                id="count"
                value={numberOfCases}
                onChange={(e) => setNumberOfCases(Number(e.target.value))}
              >
                {COUNT_OPTIONS.map((count) => (
                  <option key={count} value={count}>{count} test cases</option>
                ))}
              </select>
            </div>
          </div>

          <div className="actions">
            <button type="button" className="btn btn-primary" onClick={onGenerate} disabled={isLoading}>
              {isLoading ? 'Generating...' : 'Generate Test Cases'}
            </button>
            <button type="button" className="btn" onClick={() => onExport('csv')}>Download CSV</button>
            <button type="button" className="btn" onClick={() => onExport('xlsx')}>Download Excel</button>
          </div>

          {error && <p className="error">{error}</p>}
        </section>

        <TestCaseTable cases={testCases} />
      </main>
    </div>
  );
}

export default App;
