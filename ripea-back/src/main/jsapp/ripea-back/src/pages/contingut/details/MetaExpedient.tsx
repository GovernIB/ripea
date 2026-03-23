import {Grid2 as Grid} from "@mui/material";
import {StyledLabel} from "../../../components/StyledLabel.tsx";

export const MultiplicitatStyled = (props:any) => {
    const {multiplicitat} = props;

    return <StyledLabel className={'multiplicitat'} backgroundColor={'info'} color={'white'}>{multiplicitat}</StyledLabel>
}

const MetaExpedient = (props:any) => {
    const {entity} = props;
    let multiplicitat;

    switch (entity?.multiplicitat) {
        case "M_0_1":
            multiplicitat = "0..1"
            break;
        case "M_0_N":
            multiplicitat = "0..N"
            break;
        case "M_1":
            multiplicitat = "1"
            break;
        case "M_1_N":
            multiplicitat = "1..N"
            break;
        default:
            multiplicitat = entity?.multiplicitat;
            break;
    }

    return <Grid width={'330px'} display={"flex"} alignItems={"center"} justifyContent={'space-between'}>
        {entity?.nom}
        <MultiplicitatStyled multiplicitat={multiplicitat}/>
    </Grid>
}
export default MetaExpedient;