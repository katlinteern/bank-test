import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css'
import CreateInvestmentForm from './components/CreateInvestmentForm';
import NavBar from "./components/NavBar/NavBar";
import InvestmentList from "./components/InvestmentList/InvestmentList";
import Investments from "./components/Investments";
import { InvestmentListProvider } from "./context/InvestmentContext";

function App() {
  return (
    <Router>
      <InvestmentListProvider>
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
                <Route path="/new" element={<CreateInvestmentForm />} />
                <Route path="/" element={<Investments />}>
                  <Route index element={<InvestmentList />} />
                </Route>
              </Routes>
            </div>
          </div>
        </div>
      </InvestmentListProvider>
    </Router>
  );
}

export default App;
