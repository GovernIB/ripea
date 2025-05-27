const commonProps = { width: '100%', height: '500px', border: '1px solid lightgray', borderRadius: '4px' }

const Iframe = (props:any) => {
    const { src, hidden, ...other } = props

    if(!src || hidden) {
        return <></>
    }

    return <iframe src={src} {...other} style={commonProps}/>
}
export default Iframe;