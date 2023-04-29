import { Navigate, Outlet, Route, Routes, useNavigate } from 'react-router-dom';
import Login from './views/Login';
import Home from './views/Home';
import Admin from './views/Configure';
import { useRef, useState } from 'react';
import { Toast } from 'primereact/toast';
import PrivateRoute from './components/PrivateRoute';
import Navigation from './components/Navigation';
import WorldView from './views/World';
import { User } from './models/UserModels';
import { ProgressSpinner } from 'primereact/progressspinner';
import PrivateRoute2 from './components/PrivateRoute2';

function App() {
  const toast = useRef(null);

  return (
    <>
        <Routes>
          <Route index path='/' element={<Login toast={toast} />}></Route>
          
          <Route element={<PrivateRoute2 roles={[0, 1]}/>}>
              <Route path="/home" element={<>
                  <Navigation toast={toast}></Navigation>
                  <Home toast={toast}></Home>
                </>}>
              </Route>
              <Route path="/world" element={<Navigate to="/home"/>}>
                <Route path=":worldId" element={<WorldView toast={toast}/>}/>
              </Route>
          </Route>

          <Route element={<PrivateRoute2 roles={[1]}/>}>
            <Route path="/configure" element={<>
              <Navigation toast={toast}></Navigation>
              <Admin toast={toast}></Admin>
            </>}></Route>
          </Route>

          {/* <Route element={<PrivateRoute path='/home' user={user}/>}>
            <Route path='/home' element={<>
              <Navigation toast={toast} user={user}></Navigation>
              <Home toast={toast}></Home>
            </>}/>
          </Route> */}
          {/* <Route element={<PrivateRoute path='/configure' user={user}/>}>
            <Route path='/configure' element={<>
                <Navigation toast={toast} user={user}></Navigation>
                <Admin toast={toast}></Admin>
            </>}/>
          </Route> */}
          {/* <Route path='/world' element={<Navigate to="/home" replace></Navigate>}/> */}
          {/* 
          
          <ProtectedRoute path='/world/:worldId>

          */}
          {/* <Route path='/world' element={<PrivateRoute path='/world' returnPath='/home' user={user}/>}>
              <Route path=':worldId' element={<>
                <WorldView toast={toast} user={user}></WorldView>
                </>
              }/>
          </Route> */}
          
        </Routes>
        <Toast ref={toast}/>
    </>
  );
}

export default App;
