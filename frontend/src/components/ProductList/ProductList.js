import React, { useContext, useEffect } from 'react';
import ProductTableRow from '../ProductTableRow';
import { ProductContext } from '../../context/ProductContext';
import { getProducts } from '../../services/ApiService';
import { NavLink } from 'react-router-dom';
import './ProductList.css';

export default function ProductList() {
  const { products, updateProducts } = useContext(ProductContext);

  useEffect(() => {
    async function fetchData() {
      try {
        const products = await getProducts();
        updateProducts(products);
      } catch (error) {
        console.error('Error fetching products:', error);
      }
    }

    fetchData();
  }, []);

  return (
    <div>
      <div className="summary-container">
        <h2>Investment Summary</h2>
        <p>Your current investments have a total return of $XX,XXX.</p>
        <p>Manage your investments effectively: add, edit, or remove products from the list below.</p>
      </div>

      {/* Wrapper for the button to align it to the right */}
      <div className="button-container">
        <NavLink className="btn btn-primary" to="/new">Add</NavLink>
      </div>

      {/* Add a title for the table */}
      <div className="table-title">
        <h2>Table</h2>
      </div>

      <table className="table table-striped">
        <thead>
          <tr>
            <th scope="col">Title</th>
            <th scope="col">Price</th>
            <th scope="col">Quantity</th>
          </tr>
        </thead>
        <tbody>
          {products.map(({ id, ...product }) => (
            <ProductTableRow key={id} {...product} />
          ))}
        </tbody>
      </table>
    </div>
  );
}
