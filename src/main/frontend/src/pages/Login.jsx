import React, { useState } from "react";
import { Card } from "primereact/card";
import { Button } from "primereact/button";
import { InputText } from "primereact/inputtext";
import { Password } from "primereact/password";

const Login = () => {
    /* 
    The way this login system will function is:

    - user will go to Discord and run the command /pmlogin
      - behind the scenes, this will check if the current user running the command has a minecraft account linked with their discord account via authhub
      - if the user has not linked their discord and minecraft account together, they will not be able to recieve an OTP
      - if the user has linked their accounts, they will recieve a DM from the plot manager bot that will contain an OTP to be used for this login
    - they will come to this login page, put their current minecraft username in the username input box
    - they will put their current OTP in the password box
    - they will login
      - the user permissions will be set off of their rank in game. some sort of middleware will have to check their current rank after every action they perform on the server


    */
    const [username, setUsername] = useState("");
    const [otp, setOtp] = useState("");
    const [loading, setLoading] = useState(false);

    const performLogin = () => {
        setLoading(true);
        setTimeout(() => {
            setLoading(false);
        }, 3000);
    }

    const footer = <span>
        <Button label="Login" loading={loading} loadingIcon="pi pi-spin pi-spinner" onClick={() => performLogin()}></Button>
    </span>;

    return (
        <Card title="Login" footer={footer} className="sm:w-full md:w-9 lg:w-6 xl:w-3 p-3">
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
    )
}

export default Login;