import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css'
import CreateProductForm from './components/CreateProductForm';
import NavBar from "./components/NavBar/NavBar";
import ProductList from "./components/ProductList/ProductList";
import Products from "./components/Products";
import { ProductListProvider } from "./context/ProductContext";

function App() {
  return (
    <Router>
      <ProductListProvider>
        <div className="container-fluid">
          <div className="row">
            {/* Left-hand side: Vertical Navbar */}
            <div className="col-md-2">
              <NavBar />
            </div>

            {/* Right-hand side: Main Content */}
            <div className="col-md-10">
              <div className="header-container">
                <h2>My portfolio</h2>
              </div>
              <Routes>
                <Route path="/new" element={<CreateProductForm />} />
                <Route path="/" element={<Products />}>
                  <Route index element={<ProductList />} />
                </Route>
              </Routes>
            </div>
          </div>
        </div>
      </ProductListProvider>
    </Router>
  );
}

export default App;
