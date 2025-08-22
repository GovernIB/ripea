import {Grid, Typography} from "@mui/material";

const style = {
    border: '1px solid lightgray',
    display: 'flex',
    alignItems: 'center',
    width: 'max-content',
    padding: '1px 4px',
    fontSize: '11px',
    fontWeight: '500',
    borderRadius: '2px',
    color: 'white',
    backgroundColor: 'info.main',
}

export const MultiplicitatStyled = (props:any) => {
    const {multiplicitat, sx} = props;

    return <Typography variant="caption" sx={{...style, ...sx}}>
        {multiplicitat}
    </Typography>
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
        <MultiplicitatStyled multiplicitat={multiplicitat} sx={{display: 'flex', justifySelf: 'end'}}/>
    </Grid>
}
export default MetaExpedient;