import React, { Component, useEffect, useState } from "react";
import ProgressBar from "primereact/progressbar";
import { Redirect } from "react-router-dom";

/**
 * @param {{component: Component}} props 
 */
const PrivateRoute = ({user, component}) => {

    if (user === undefined || user === null) return <ProgressBar mode="indeterminate"></ProgressBar>
    else if (user) {
        const CompTemp = component;
        return <CompTemp/>
    } else return <Redirect to="/"/>
}

export default PrivateRoute;