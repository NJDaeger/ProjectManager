import { Avatar } from "primereact/avatar";
import { Button } from "primereact/button";
import { Dialog } from "primereact/dialog";
import { Card } from "primereact/card";
import { InputText } from "primereact/inputtext";
import { ProgressSpinner } from "primereact/progressspinner";
import { Toast } from "primereact/toast";
import React, { useEffect, useState } from "react";
import { MutableRefObject } from "react";
import { useNavigate } from "react-router-dom";
import { BasicWorldList } from "../models/WorldModels";
import { NO_BACKEND } from "../services/ApiConstants";
import { verifyAuthorized } from "../services/AuthService";
import { getWorlds } from "../services/WorldService";
import { Dropdown } from "primereact/dropdown";
import { MultiSelect } from "primereact/multiselect";

interface HomeProps {
    toast: MutableRefObject<null|Toast>
}

interface NonRegisteredWorld {
    worldName: string,
    worldId: string
}

interface CreateWorldRequest {
    selectedWorld: NonRegisteredWorld|null
}

const Home = (props: HomeProps) => {
    const [worldList, setWorldList] = useState<BasicWorldList|null>(null);
    const [createWorldModal, setCreateWorldModal] = useState<boolean>(false);
    const [search, setSearch] = useState<string|null>(null);
    const [loadingWorlds, setLoadingWorlds] = useState<boolean>(false);
    const [nonRegisteredWorlds, setNonRegisteredWorlds] = useState<NonRegisteredWorld[]>([]);
    const [createWorldRequest, setCreateWorldRequest] = useState<CreateWorldRequest|null>(null);
    const [creatingWorld, setCreatingWorld] = useState<boolean>(false);
    
    const navigate = useNavigate();
    useEffect(() => {
        if (NO_BACKEND) {
            setLoadingWorlds(true);
            setTimeout(() => {
                setLoadingWorlds(false);
                setWorldList({worlds: [{plotCount: 10, worldId: "test1", worldName: "World"}]});
                setNonRegisteredWorlds([{worldId: "test2", worldName: "Test 2"}])
            }, 5000);
            return;
        }
        verifyAuthorized({path: '/home'}).then(() => {
            setLoadingWorlds(true);
            getWorlds().then((res) => {
                setWorldList(res);
                setLoadingWorlds(false);
            }).catch((err) => {
                props.toast.current?.show({severity: "error", content: err.message});
                setLoadingWorlds(false);
            });
        }).catch((err) => {
            props.toast.current?.show({severity: "error", content: "You are not authorized for this action."})
            navigate('/');
        });
    }, []);

    const createWorldButton = (worldName: string, openProjects: number, worldId: string) : JSX.Element => {
        return <a className={"world-list-link p-button-text p-button-raised hover:shadow-8 shadow-3 m-3 w-18rem h-7rem p-3 flex align-items-center no-underline transition-duration-200"} id={worldId} key={worldId} href={'/world/' + worldId}>
            <div className={"w-5rem h-5rem border-round flex-none bg-cyan-500 animation-fill-forwards fadein"}>
            </div>
            <div className="world-info flex flex-column ml-3 text-left w-10rem overflow-hidden text-overflow-ellipsis white-space-nowrap">
                <span className="font-bold text-gray-200 text-overflow-ellipsis white-space-nowrap" style={{fontSize:"16pt"}}>{worldName}</span>
                <span className="text-gray-400">{openProjects} open project{openProjects === 1 ? "" : "s"}</span>       
            </div>
        </a>;
    }

    const createCardContent = () => {
        if (loadingWorlds || worldList == null) return <div className="text-center w-full align-self-center"><ProgressSpinner strokeWidth="3" animationDuration="1s" className="absolute w-5rem h-5rem border-round flex-none -ml-6"></ProgressSpinner></div>;
        var worlds = worldList.worlds;
        if (search !== null) worlds = worlds.filter(world => world.worldId.toLowerCase().includes(search.toLowerCase()) || world.worldName.toLowerCase().includes(search.toLowerCase()));
        var buttons = worlds.map(worldInfo => createWorldButton(worldInfo.worldName, worldInfo.plotCount, worldInfo.worldId));
        buttons.push(
            <Button className={"p-button-text hover:shadow-8 m-3 w-18rem h-7rem p-3 shadow-3 "} id={"new-world-button"} key={"new-world-button"} onClick={() => modalOpen()}>
                <div className={"w-5rem h-5rem border-round flex-none pi pi-plus text-6xl mt-5 animation-fill-forwards fadein"}>
                </div>
                <div className="world-info flex flex-column ml-3 text-left w-10rem overflow-hidden text-overflow-ellipsis white-space-nowrap">
                    <span className="font-bold text-gray-200 text-overflow-ellipsis white-space-nowrap" style={{fontSize:"16pt"}}>Add new world</span>
                </div>
            </Button>
        );
        return <div className="-m-2 p-2 flex flex-wrap overflow-auto w-full">
            { buttons }
        </div>
    }

    const modalOpen = () => {
        setCreateWorldModal(true);
        setCreateWorldRequest({selectedWorld: null})
    }

    const modalClose = () => {
        setCreateWorldModal(false);
        setCreateWorldRequest(null);
    }

    const modalCreate = () => {
        setCreatingWorld(true);
        if (NO_BACKEND) {
            setTimeout(() => {
                setCreatingWorld(false);
                setCreateWorldModal(false);
            }, 5000);
            return;
        }
    }

    const onSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        var val = e.target.value;
        if (val === null || val === undefined || val === "") setSearch(null);
        else setSearch(val);
    }

    const title: JSX.Element = <>
        <div className="flex flex-row justify-content-between mx-4 align-items-center my-1">
            <h2 className="my-1">World Selection List</h2>
            <span className="w-4">
                <span className="p-float-label w-full">
                    <InputText id="search" className="w-full" disabled={loadingWorlds || worldList?.worlds.length == 0} onChange={onSearchChange}/>
                    <label htmlFor="search">Search</label>
                </span>
            </span>
        </div>
    </>;

    return <>
        <div className="w-full pt-8 pb-8 h-full">
            <Card title={title} className="sm:w-full md:w-10 mx-auto shadow-4" id="world-list">
                <hr className="mx-4"/>
                
                <div className="flex m-4" id="world-container">
                    {createCardContent()}
                </div>
            </Card>
        </div>
        <Dialog resizable={false} draggable={false} header="Add new world" visible={createWorldModal} onHide={() => modalClose()}footer={<div>
            <Button label="Create" icon="pi pi-check" onClick={() => modalCreate()} disabled={creatingWorld || createWorldRequest?.selectedWorld == null} loading={creatingWorld}></Button>
            <Button label="Cancel" icon="pi pi-times" className="p-button-text" onClick={() => modalClose()} autoFocus></Button>
        </div>}>
            <Dropdown className="w-full" inputMode="search" options={nonRegisteredWorlds} optionLabel="worldName" placeholder="Select a world" emptyMessage="No worlds can be added." onChange={(e) => setCreateWorldRequest({selectedWorld: e.value})} value={createWorldRequest?.selectedWorld}></Dropdown>
            {/* <MultiSelect className="w-full mt-3" options={createWorldRequest?.selectedWorld?.dimensions} optionLabel="dimensionName" optionValue="dimensionId" min={1} onChange={(e) => createWorldRequest != null ? setCreateWorldRequest({selectedDimensions: e.value, selectedWorld: createWorldRequest.selectedWorld}) : {}} disabled={createWorldRequest?.selectedWorld == null} placeholder="Select dimensions" showSelectAll={false} value={createWorldRequest?.selectedDimensions}></MultiSelect> */}
        </Dialog>
    </>;
}

export default Home;