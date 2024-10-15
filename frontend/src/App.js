import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import './App.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import CreateInvestmentForm from './components/CreateInvestmentForm';
import NavBar from "./components/NavBar/NavBar";
import Investments from "./components/Investments"; // This now includes both InvestmentSummary and InvestmentList
import { InvestmentListProvider } from "./context/InvestmentContext";

function App() {
  return (
    <Router>
      <InvestmentListProvider>
        <div className="container-fluid">
          <div className="row">
            <div className="col-md-2">
              <NavBar />
            </div>

            <div className="col-md-10">
              <div className="header-container">
                <h2>My portfolio</h2>
              </div>
              <Routes>
                <Route path="/" element={<Investments />} /> {/* This route now includes both components */}
                <Route path="/new" element={<CreateInvestmentForm />} />
              </Routes>
            </div>
          </div>
        </div>
      </InvestmentListProvider>
    </Router>
  );
}

export default App;
