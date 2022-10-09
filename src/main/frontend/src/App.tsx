import { Outlet, Route, Routes } from 'react-router-dom';
import Login from './views/Login';
import Home from './views/Home';
import { useRef } from 'react';
import { Toast } from 'primereact/toast';
import PrivateRoute from './components/PrivateRoute';

function App() {
  const toast = useRef(null);
  return (
    <>
        <Routes>
          <Route path='/' element={<Login toast={toast}></Login>}></Route>
          <Route element={
            <PrivateRoute path='/home'>
              <Outlet/>
            </PrivateRoute>
          }>
            <Route path='/home' element={<Home toast={toast}></Home>}></Route>
          </Route>
        </Routes>
        <Toast ref={toast}/>
    </>
  );
}

export default App;
