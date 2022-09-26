import logo from './logo.svg';
import './App.css';
import { React } from 'react';
import Login from "./pages/Login";
import Home from './pages/Home';
import { useRoutes } from 'react-router-dom';
import { useState } from 'react';

function App() {
  const [loggedIn, setLoggedIn] = useState(false);
  let routes = useRoutes([
    {pathname: "/", element: <Login/>, path:"/"},
    {pathname: "/home", element: <Home/>, path:"/home"}
  ])
  return routes;
}

export default App;
