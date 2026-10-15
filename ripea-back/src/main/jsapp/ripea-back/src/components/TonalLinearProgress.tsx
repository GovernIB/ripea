import {Box, LinearProgress, LinearProgressProps, SxProps, Theme} from "@mui/material";
import {TonalColor, tonalProgress} from "../util/tonal.ts";

type TonalLinearProgressProps = Omit<LinearProgressProps, 'color' | 'variant'> & {
    color?: TonalColor;
    /** Estils del contenidor (posició, marges...) */
    boxSx?: SxProps<Theme>;
    /** Text sobre la barra, normalment el percentatge. Obligatori a la pràctica: el color tonal sol no té prou contrast. */
    children?: React.ReactNode;
}

/** Barra de progrés en color tonal amb el valor escrit a sobre, en el color de text del tema. */
const TonalLinearProgress = (props: TonalLinearProgressProps) => {
    const {color = 'success', boxSx, sx, children, ...other} = props;

    return <Box position="relative" width="100%" sx={boxSx}>
        <LinearProgress
            variant="determinate"
            {...other}
            sx={[
                {width: '100%', borderRadius: '4px'},
                tonalProgress(color),
                ...(sx === undefined ? [] : Array.isArray(sx) ? sx : [sx]),
            ]}
        />
        <Box
            color="text.primary"
            fontWeight={500}
            position="absolute"
            top={0}
            left={0}
            width="100%"
            height="100%"
            display="flex"
            alignItems="center"
            justifyContent="center"
            aria-hidden
        >
            {children}
        </Box>
    </Box>
}

export default TonalLinearProgress;
