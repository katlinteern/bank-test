import React, {useContext, useRef} from "react";
import {createInvestment} from "../services/ApiService";
import {useNavigate} from'react-router-dom';
import {InvestmentContext} from "../context/InvestmentContext";
import { NavLink } from 'react-router-dom';

export default function CreateInvestmentForm() {

  const navigate = useNavigate();
  const titleRef = useRef();
  const priceRef = useRef();
  const quantityRef = useRef();

  const {addInvestment} = useContext(InvestmentContext);

  async function add(target) {
    target.preventDefault();

    try {

      const newInvestment = {
        title: titleRef.current.value,
        price: priceRef.current.value,
        quantity: quantityRef.current.value
      };

      console.log(newInvestment)

      const response = await createInvestment(newInvestment);
      console.log(response)
      addInvestment(response);
      navigate(`/${response.id}`);

    } catch (error) {
      console.error('Error', error);
    }
  }

  return(
    <form>
      {/* Breadcrumbs */}
      <nav aria-label="breadcrumb">
        <ol className="breadcrumb">
          <li className="breadcrumb-item">
            <NavLink to="/">Investments</NavLink>
          </li>
        </ol>
      </nav>
      <div className="mb-3 mt-5">
        <label htmlFor="title" className="form-label">Title</label>
        <input ref={titleRef} type="text" className="form-control" id="title" aria-describedby="titleHelp" />
        <div id="titleHelp" className="form-text">Input the investment title here.</div>
      </div>
      <div className="mb-3">

        <div className="row">
          <div className="col-6">
            <label htmlFor="price" className="form-label">Price</label>
            <input ref={priceRef} type="number" className="form-control" id="price" />
          </div>
          <div className="col-6">
            <label htmlFor="quantity" className="form-label">Quantity</label>
            <input ref={quantityRef} type="number" className="form-control" id="quantity" />
          </div>
        </div>

      </div>
      <button onClick={add} type="submit" className="btn btn-primary">Add</button>
    </form>
  );

}