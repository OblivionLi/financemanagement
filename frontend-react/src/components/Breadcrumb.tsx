import React from 'react';
import "../../styles.css";
import {Link} from "react-router-dom";

interface BreadCrumbProps {
    page: string;
}

const Breadcrumb: React.FC<BreadCrumbProps> = ({ page }) => {
    return (
        <nav className="breadcrumb-nav" aria-label="breadcrumb">
            <ol className="breadcrumb">
                <li className="breadcrumb-item"><Link to="/">Home</Link></li>
                <li className="breadcrumb-item active" aria-current="page">{page}</li>
            </ol>
        </nav>
    );
};

export default Breadcrumb;