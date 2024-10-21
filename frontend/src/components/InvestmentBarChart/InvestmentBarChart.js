import React from 'react';
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import { Bar } from 'react-chartjs-2';

ChartJS.register(
  CategoryScale, 
  LinearScale,   
  BarElement,     
  Title,        
  Tooltip,       
  Legend       
);

const InvestmentBarChart = ({ investments }) => {
  const labels = investments.map(investment => investment.name);

  const data = {
    labels,
    datasets: [
      {
        label: 'Total Value (in €)',
        data: investments.map(investment => investment.totalValue),
        backgroundColor: '#8884d8',
        yAxisID: 'y',  
      },
      {
        label: 'Profitability (%)',
        data: investments.map(investment => investment.profitability),
        backgroundColor: '#82ca9d',
        yAxisID: 'y1', 
      },
    ],
  };

  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: 'Investments Overview (Total Value & Profitability)',
      },
    },
    scales: {
      y: {
        type: 'linear',
        position: 'left',
        title: {
          display: true,
          text: 'Total Value (in €)',
        },
      },
      y1: {
        type: 'linear',
        position: 'right',
        title: {
          display: true,
          text: 'Profitability (%)',
        },
        grid: {
          drawOnChartArea: false, 
        },
      },
    },
  };

  return <Bar data={data} options={options} />;
};

export default InvestmentBarChart;
