import axios from "axios";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const apiUrl = `${baseURL}/api/investments`;

const userId = 1; // TODO change when developing login

export const getInvestments = async () => {
  try {
    const response = await axios.get(apiUrl);
    return response.data;
  } catch (error) {
    throw error;
  }
}

export const createInvestment = async (investment) => {
  try {
    const response = await axios.post(apiUrl, investment);
    return response.data;
  } catch (error) {
    throw error;
  }
}

export const getInvestmentsByUserId = async () => {
  try {
    const response = await axios.get(`${apiUrl}/user/${userId}`);
    return response.data;
  } catch (error) {
    throw error;
  }
}

export const getPortfolioProfitablity = async () => {
  try {
    const response = await axios.get(`${apiUrl}/profitability/`);
    console.log(response, "resp");
    return response.data;
  } catch (error) {
    throw error;
  }
}