import { useEffect, useState } from "react";
import { ProgressSpinner } from 'primereact/progressspinner';
import { Navigate } from "react-router-dom";
import { verifyAuthorized } from "../services/AuthService";

interface PrivateRouteProps {
    path: string,
    children: any
}

const PrivateRoute = (props: PrivateRouteProps) => {
    const [authorized, setAuthorized] = useState<boolean|undefined>(undefined);
    
    // console.log("TEST ");
    useEffect(() => {
        if (authorized === undefined) {
            // console.log("EFFECT");
            verifyAuthorized({path: props.path }).then(res => {
                // console.log("Authorized!")
                setAuthorized(true);
            }).catch(err => {
                // console.log("Not authorized.")
                setAuthorized(false);
            });
        }
    }, []);

    if (authorized === undefined) return <ProgressSpinner className="flex" style={{height:"90vh"}}/>
    else if (!authorized) return <Navigate to="/"/>
    return <>{props.children}</>
}

export default PrivateRoute;