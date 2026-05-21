import {IconButton, IconButtonProps} from "@mui/material";
import {useNavigate} from "react-router-dom";

type LinkIconProps = IconButtonProps & {
    to: string,
}
const LinkIcon = (props:LinkIconProps) => {
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