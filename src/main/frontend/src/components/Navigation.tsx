import { Toast } from "primereact/toast";
import { TabMenu } from "primereact/tabmenu";
import { Menubar, MenubarStartTemplate } from "primereact/menubar"
import { Avatar } from "primereact/avatar";
import { useEffect, useState } from "react";
import { MutableRefObject } from "react";
import { useNavigate } from "react-router-dom";
import { verifyAuthorized } from "../services/AuthService";
import { MenuItem } from "primereact/menuitem";
import { Button } from "primereact/button";

interface NavigationProps {
    toast: MutableRefObject<null|Toast>
}

const Navigation = (props: NavigationProps) => {
    const [activeIndex, setActiveIndex] = useState(0);
    const navigate = useNavigate();

    const navitems: MenuItem[] = [
        {label: 'Home', icon: 'pi pi-home'},
        {label: 'Analytics', icon: 'pi pi-chart-bar'}
    ];

    return <>
        <nav className="navbar bg-primary-reverse shadow-4">
            <Button className="ml-3 p-button-primary p-button-text p-0 m-2"><span className="mr-1 ml-1 sm:mr-2 sm:ml-2 flex align-items-center font-bold text-700"><Avatar label="PM"></Avatar><span className="pl-2 hidden sm:inline">ProjectManager</span></span></Button>
            <span className="vertical-bar mt-2 mb-2 mr-3 ml-2 bg-gray-500"></span>
            <TabMenu model={navitems} activeIndex={activeIndex} onTabChange={e => setActiveIndex(e.index)} className="mr-auto bg-transparent"></TabMenu> 
            <span className="vertical-bar mt-2 mb-2 mr-2 ml-3 bg-gray-500"></span>
            <Button className="mr-3 p-button-primary p-button-text p-0 m-2"><span className="mr-1 ml-1 sm:mr-2 sm:ml-2 flex align-items-center float-right font-bold text-700"><span className="pr-2 hidden sm:inline">NJDaeger</span><Avatar label="N" size="normal"></Avatar></span></Button>
        </nav>
    </>;
}

export default Navigation;