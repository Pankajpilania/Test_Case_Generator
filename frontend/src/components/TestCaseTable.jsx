import React from 'react';

function TestCaseTable({ cases }) {
  if (!cases?.length) {
    return null;
  }

  return (
    <div className="table-wrapper">
      <h2>Generated Test Cases</h2>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Description</th>
            <th>Preconditions</th>
            <th>Steps</th>
            <th>Expected Result</th>
            <th>Type</th>
          </tr>
        </thead>
        <tbody>
          {cases.map((testCase) => (
            <tr key={testCase.testCaseId}>
              <td>{testCase.testCaseId}</td>
              <td>{testCase.title}</td>
              <td>{testCase.description}</td>
              <td>{testCase.preconditions}</td>
              <td>
                <ol>
                  {testCase.steps.map((step, index) => (
                    <li key={`${testCase.testCaseId}-step-${index}`}>{step}</li>
                  ))}
                </ol>
              </td>
              <td>{testCase.expectedResult}</td>
              <td>
                <span className={`pill ${testCase.type.toLowerCase()}`}>{testCase.type}</span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default TestCaseTable;
