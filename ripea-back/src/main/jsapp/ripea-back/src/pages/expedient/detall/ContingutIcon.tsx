import {Grid, Icon} from "@mui/material";

const ContingutIcon = (props:any) => {
    const {entity, children} = props;

    // console.log(entity)
    const extension = entity?.fitxerNom ?entity?.fitxerNom.split('.').reverse()[0] :null;
    return <Grid justifyContent={"center"}>
        {entity?.expedient && entity?.estat == 'OBERT' && <Icon>O</Icon>}
        {entity?.expedient && entity?.estat != 'OBERT' && <Icon>T</Icon>}

        {extension == 'pdf' && <Icon>picture_as_pdf</Icon>}
        {extension == 'doc' && <Icon></Icon>}
        {extension == 'xls' && <Icon></Icon>}
        {extension == 'zip' && <Icon>folder_zip</Icon>}
        {extension == 'xsig' && <Icon></Icon>}
        {(extension == 'jpeg' || extension == 'png') && <Icon>photo</Icon>}
        {extension == 'txt' && <Icon></Icon>}
        {(extension == 'mp3' || extension == 'wav') && <Icon>audio_file</Icon>}
        {(extension == 'mpeg' || extension == 'avi') && <Icon>video_file</Icon>}

        {children}
    </Grid>
}
export default ContingutIcon;