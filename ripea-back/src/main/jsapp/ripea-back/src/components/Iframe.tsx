const commonProps = { width: '100%', height: '500px', border: '1px solid lightgray', borderRadius: '4px' }

const Iframe = (props:any) => {
    const { src, hidden, style, ...other } = props

    if(!src || hidden) {
        return <></>
    }

    return <iframe src={src} {...other} style={{...commonProps, ...style}}/>
}
export default Iframe;