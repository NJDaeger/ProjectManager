import App from './App.tsx';
import ReactDOM from 'react-dom/client';
import PrimeReact from 'primereact/api';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter } from 'react-router-dom';

import "primereact/resources/themes/lara-light-indigo/theme.css";  //theme
import "primereact/resources/primereact.min.css";                  //core css
import "/node_modules/primeflex/primeflex.css"
import "primeicons/primeicons.css";                                //icons
import "./styles/style.scss";
import './index.css';

PrimeReact.ripple = true;

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
  <BrowserRouter>
    <App></App>
  </BrowserRouter>
);

// If you want to start measuring performance in your app, pass a function
// to log results (for example: reportWebVitals(console.log))
// or send to an analytics endpoint. Learn more: https://bit.ly/CRA-vitals
reportWebVitals();
