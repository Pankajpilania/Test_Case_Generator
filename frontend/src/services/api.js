import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

export const generateTestCases = async (payload) => {
  const { data } = await api.post('/generate-testcases', payload);
  return data;
};

export const exportFile = async (payload, format) => {
  const endpoint = format === 'xlsx' ? '/generate-testcases/export/xlsx' : '/generate-testcases/export/csv';
  const response = await api.post(endpoint, payload, { responseType: 'blob' });

  const blob = new Blob([response.data], {
    type: format === 'xlsx'
      ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      : 'text/csv'
  });

  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = `test-cases.${format}`;
  document.body.appendChild(link);
  link.click();
  link.remove();
  window.URL.revokeObjectURL(url);
};
