import React, { createContext, useState } from 'react';

export const ProductContext = createContext();

export const ProductListProvider = ({ children }) => {
  const [products, setProducts] = useState([]);
  const [product] = useState({});

  const updateProducts = (products) => {
    setProducts(products)
  }

  const addProduct = (product) => {
    setProducts([... products, product]);
  }

  const removeProductById = (id) => {
    const newProducts = products.filter((product) => product.id !== id);
    setProducts(newProducts);
  }

  return (
    <ProductContext.Provider value={{ products, product, removeProductById, addProduct, updateProducts }}>
      {children}
    </ProductContext.Provider>
  );
}