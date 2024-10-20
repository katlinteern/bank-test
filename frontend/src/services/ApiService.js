
const baseURL = process.env.REACT_APP_API_BASE_URL;

const apiUrl = `${baseURL}/api/investments`;

const userId = 1; // TODO change when developing login

export const getUserInvestments = async () => {
  try {
    const response = await fetch(`${apiUrl}/user/${userId}`);
    const data = await response.json();
    return data;
  } catch (error) {
    throw error;
  }
}

export const getUserInvestmentSummary = async () => {
  try {
    const response = await fetch(`${apiUrl}/user/${userId}/summary`);
    const data = await response.json();
    return data;
  } catch (error) {
    throw error;
  }
}
