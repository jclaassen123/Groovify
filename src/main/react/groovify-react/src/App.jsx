import {useEffect, useState} from 'react'
import './App.css'

function App() {

    // If the back end domain is not running, "Default value" is printed to the page.
    const [title, setTitle] = useState('Default value')

    /*

     */
    useEffect(() => {
        fetch("http://localhost:8080/").then(response=>response.text())
            .then(text=>setTitle(text))
            .catch(error=>console.log("Error fetching ", error))
    }, []);

    return (
        <>
            <head>
                <title>Groovify</title>
            </head>
            <body>
            <h1>{title}</h1>
            <a href="" className="loginButton"><h2>Login</h2></a>
            <a href="" className="loginButton"><h2>Register</h2></a>
            </body>
        </>
    )
}

export default App
