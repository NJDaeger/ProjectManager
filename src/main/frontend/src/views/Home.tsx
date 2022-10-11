import { Toast } from "primereact/toast";
import { useEffect } from "react";
import { MutableRefObject } from "react";
import { useNavigate } from "react-router-dom";
import { verifyAuthorized } from "../services/AuthService";

interface HomeProps {
    toast: MutableRefObject<null|Toast>
}

const Home = (props: HomeProps) => {
    const navigate = useNavigate();
    useEffect(() => {
        return;
        verifyAuthorized({path: '/home'}).catch((err) => {
            props.toast.current?.show({severity: "error", content: "You are not authorized for this action."})
            navigate('/');
        });
    }, []);

    return <>

    </>;
}

export default Home;