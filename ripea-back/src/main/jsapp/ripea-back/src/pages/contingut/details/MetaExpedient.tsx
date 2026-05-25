import {Grid} from "@mui/material";
import {StyledLabel} from "../../../components/StyledLabel.tsx";

export const formatMultiplicitat = (raw: string): string => {
    switch (raw) {
        case "M_0_1": return "0..1";
        case "M_0_N": return "0..N";
        case "M_1":   return "1";
        case "M_1_N": return "1..N";
        default:      return raw;
    }
}

export const MultiplicitatStyled = (props:any) => {
    const {multiplicitat} = props;

    return <StyledLabel className={'multiplicitat'} backgroundColor={'info'} color={'white'}>{multiplicitat}</StyledLabel>
}

const MetaExpedient = (props:any) => {
    const {entity, hideMultiplicitat} = props;
    const multiplicitat = formatMultiplicitat(entity?.multiplicitat);

    return <Grid
        width={hideMultiplicitat ? 'auto' : '330px'}
        display={"flex"}
        alignItems={"center"}
        justifyContent={hideMultiplicitat ? 'flex-start' : 'space-between'}
    >
        {entity?.nom}
        {!hideMultiplicitat && <MultiplicitatStyled multiplicitat={multiplicitat}/>}
    </Grid>
}
export default MetaExpedient;