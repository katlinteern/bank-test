
export default function ProductTableRow ({id, title, price, quantity}) {

  return(
    <tr>
      <td>{title}</td>
      <td>{price}</td>
      <td>{quantity}</td>
    </tr>
  );
}