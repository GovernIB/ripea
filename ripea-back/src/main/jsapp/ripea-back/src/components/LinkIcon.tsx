import {IconButton} from "@mui/material";
import {useNavigate} from "react-router-dom";

const LinkIcon = (props:any) => {
    const navigate = useNavigate();
    return <IconButton
        {...props}
        onClick={(e:any) => {
            e.stopPropagation();
            if (props?.to) {
                navigate(props?.to);
            }
        }}
    />
}
export default LinkIcon;