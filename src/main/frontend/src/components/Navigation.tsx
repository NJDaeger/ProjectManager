import { Toast } from "primereact/toast";
import { TabPanel, TabView } from "primereact/tabview";
import { Menubar, MenubarStartTemplate } from "primereact/menubar"
import { Avatar } from "primereact/avatar";
import { useEffect, useState } from "react";
import { MutableRefObject } from "react";
import { useNavigate } from "react-router-dom";
import { verifyAuthorized } from "../services/AuthService";
import { MenuItem } from "primereact/menuitem";
import { Button } from "primereact/button";
import Logo from "./Logo";
import { User } from "../models/UserModels";
import { NO_BACKEND, USER_API } from "../services/ApiConstants";
import useSession from "../hooks/useSession";

interface NavigationProps {
    toast: MutableRefObject<null|Toast>
}

interface NavigationWithWorldProps extends NavigationProps {
    worldId: string,
    worldName: string
}

interface TabItem {
    label: string,
    icon: string,
    link: string
}

const Navigation = (props: NavigationProps|NavigationWithWorldProps) => {
    const session = useSession();
    const [activeIndex, setActiveIndex] = useState(0);
    const [navItems, setNavItems] = useState<TabItem[]>([])
    const navigate = useNavigate();

    // const navitems: TabItem[] = [
    //     {label: 'Home', icon: 'pi pi-home', link: '/home'},
    //     {label: 'Configure', icon: 'pi pi-chart-bar', link: '/configure'}
    // ];

    useEffect(() => {
        setNavItems([
            {label: 'Home', icon: 'pi pi-home', link: '/home'},
            {label: 'Configure', icon: 'pi pi-chart-bar', link: '/configure'}
        ]);
        if ('worldId' in props) {
            setNavItems([
                {label: 'Home', icon: 'pi pi-home', link: '/home'},
                {label: 'Configure', icon: 'pi pi-chart-bar', link: '/configure'},
                {label: props.worldName, icon: 'pi pi-box', link: '/world/' + props.worldId}
            ])
            // navItems.push({label: props.worldName, icon: 'pi pi-box', link: '/world/' + props.worldId})
            setActiveIndex(navItems.length);
        }
    }, [])


    return <>
        <nav className="navbar bg-primary-reverse shadow-4">
            <Button className="ml-3 p-button-primary p-button-text p-0 m-2"><span className="mr-1 ml-1 sm:mr-2 sm:ml-2 flex align-items-center font-bold hover:text-700 text-500"><Logo className="mr-2" style={{width: "3.5rem", height: "2.5rem"}} containerStyle={{transform:"scale(.2)"}} textClassName="hidden"></Logo><span className="pr-l hidden sm:inline">PlotMan</span></span></Button>
            <span className="vertical-bar mt-2 mb-2 mr-3 ml-2 bg-gray-500"></span>
            <TabView activeIndex={activeIndex} onTabChange={e => {
                        setActiveIndex(e.index);
                        navigate(navItems[e.index].link);
                    }
                } className="mr-auto bg-transparent align-self-end">
                {navItems.map(i => {
                    return <TabPanel key={i.label} header={i.label} leftIcon={"mr-1 " + i.icon}></TabPanel>
                })}
            </TabView> 
            <span className="vertical-bar mt-2 mb-2 mr-2 ml-3 bg-gray-500"></span>
            <Button className="mr-3 p-button-primary p-button-text p-0 m-2"><span className="mr-1 ml-1 sm:mr-2 sm:ml-2 flex align-items-center float-right font-bold hover:text-700 text-500"><span className="pr-2 hidden sm:inline">{NO_BACKEND ? "Steve" : session?.session.username}</span><Avatar image={NO_BACKEND ? require('../steve.png') : USER_API.GET_SKULL} size="normal" style={{imageRendering: "pixelated"}}></Avatar></span></Button>
        </nav>
    </>;
}

export default Navigation;