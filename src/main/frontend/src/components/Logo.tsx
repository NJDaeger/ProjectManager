import { CSSProperties } from "react";

export interface LogoProps {
    className?: string,
    containerStyle?: CSSProperties,
    pClassName?: string,
    dClassName?: string,
    style?: CSSProperties,
    textClassName?: string
}

const Logo = (props: LogoProps) => {
    return <>
        <div className={"plotted-logo " + (props.className ?? "")} style={props.style}>
            <div className="plotted-container"  style={props.containerStyle}>
                <div className="plotted-content-container">
                    <span className="plotted-logo-text">
                        <span className={props.textClassName}>PlotMan</span>
                    </span>
                    <div className="plotted shadow-8">
                        <div className="slice">
                            <span className={props.pClassName ?? "bg-blue-800 extendBorderLeft"}></span>
                            <span className={props.pClassName ?? "bg-blue-800"}></span>
                            <span className={props.pClassName ?? "bg-blue-800"}></span>
                            <span className={props.pClassName ?? "bg-blue-800 extendBorderBottom"}></span>
                        </div>
                        <div className="slice">
                            <span className={props.pClassName ?? "bg-blue-800"}></span>
                            <span className={props.pClassName ?? "bg-blue-800"}></span>
                            <span></span>
                            <span></span>
                        </div>
                        <div className="slice">
                            <span></span>
                            <span></span>
                            <span className={props.dClassName ?? "bg-gray-600"}></span>
                            <span className={props.dClassName ?? "bg-gray-600"}></span>
                        </div>
                        <div className="slice">
                            <span className={props.dClassName ?? "bg-gray-600 extendBorderTop"}></span>
                            <span className={props.dClassName ?? "bg-gray-600"}></span>
                            <span className={props.dClassName ?? "bg-gray-600"}></span>
                            <span className={props.dClassName ?? "bg-gray-600 extendBorderRight"}></span>
                        </div>
                    </div>
                </div>
            </div>    
        </div>
    </>;
}

export default Logo;