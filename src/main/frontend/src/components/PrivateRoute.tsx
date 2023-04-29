import { useEffect, useState } from "react";
import { ProgressSpinner } from 'primereact/progressspinner';
import { Navigate, Outlet, useNavigate } from "react-router-dom";
import { verifyAuthorized } from "../services/AuthService";
import { User } from "../models/UserModels";
import { NO_BACKEND } from "../services/ApiConstants";

interface PrivateRouteProps {
    user: User|null,
    path: string,
    returnPath?: string,
    children?: any
}

const PrivateRoute = (props: PrivateRouteProps) => {
    const [authorized, setAuthorized] = useState<boolean|undefined>(undefined);
    const navigate = useNavigate();

    useEffect(() => {
        if (NO_BACKEND) return;
        if (authorized === undefined) {
            console.log("verifying authorization...")
            setAuthorized(undefined);
            verifyAuthorized({path: props.path }).then(res => {
                setAuthorized(true);
                console.log("authorized!")
            }).catch(err => {
                console.log(err);
                setAuthorized(false);
                console.log("not authorized!");
            });
        }
    }, []);

    if (NO_BACKEND) return props.children ?? <Outlet></Outlet>;

    if (authorized === undefined) return <ProgressSpinner className="flex" style={{height:"90vh"}}/>;
    else if (!authorized) return navigate("/");
    else return props.children ?? <Outlet></Outlet>;
}

export default PrivateRoute;