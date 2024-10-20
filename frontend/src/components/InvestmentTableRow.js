
export default function InvestmentTableRow ({name, totalValue, profitability}) {

  return(
    <tr>
      <td>{name}</td>
      <td>{totalValue}</td>
      <td>{profitability} %</td>
    </tr>
  );
}