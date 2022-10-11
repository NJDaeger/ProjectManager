import { useState } from "react";
import { Card } from "primereact/card";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Password } from "primereact/password";
import { Toast } from "primereact/toast";
import { login } from "../services/AuthService";
import { MutableRefObject } from "react";
import { useNavigate } from "react-router-dom";
import Logo from "../components/Logo";

interface LoginProps {
    toast: MutableRefObject<null|Toast>;
}

const Login = (props: LoginProps) => {
    const [username, setUsername] = useState("");
    const [otp, setOtp] = useState("");
    const [loading, setLoading] = useState(false);
    const [loaded, setLoaded] = useState<boolean|undefined>(undefined);
    var navigate = useNavigate();

    const performLogin = async () => {
        setLoading(true);
        login({username: username, otp: otp}).then((res) => {
            setLoading(false);
            setLoaded(true);
            props.toast.current?.show({severity: 'success', summary: `Welcome back, ${username}!`, detail: 'Login successful.'});
            navigate('/home');
        }).catch((err) => {
            setLoading(false);
            setLoaded(false);
            props.toast.current?.show({severity: 'error', summary: `We are unable to log you in at this time, please try again later.`, detail: 'Login unsuccessful.'});
        });

    }

    const footer = <span>
        <Button label="Login" loading={loading} loadingIcon="pi pi-spin pi-spinner" onClick={performLogin}></Button>
    </span>;

    return (
        <div className="w-full flex" style={{height: '100vh'}}>
            <Logo style={{zIndex: "-1"}} className="w-full flex justify-content-center mt-8"></Logo>
            <Card title="Login" footer={footer} className={"sm:w-full md:w-9 lg:w-6 xl:w-3 p-3 mx-auto my-auto text-center shadow-4 bg-primary-reverse" + (loaded ? "scaleout" : "")}>
                <div className="pb-5">
                    <span className="p-float-label">
                        <InputText id="username" value={username} onChange={(e) => setUsername(e.target.value)} className="w-full" disabled={loading}></InputText>
                        <label htmlFor="username">Java Edition Username</label>
                    </span>
                </div>
                <span className="p-float-label">
                    <Password id="otp" value={otp} onChange={(e) => setOtp(e.target.value)} className="w-full" inputClassName="w-full" feedback={false} disabled={loading}></Password>
                    <label htmlFor="otp">Current OTP</label>
                </span>
            </Card>
        </div>
    )
}

export default Login;