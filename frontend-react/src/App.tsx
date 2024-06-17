import React from 'react';
import './App.css';
import {Route, Routes} from "react-router-dom";
import Homescreen from "./screens/Homescreen";

function App() {
  return (
      <Routes>
        <Route path={"/"} element={<Homescreen/>}/>
      </Routes>
  );
}

export default App;
