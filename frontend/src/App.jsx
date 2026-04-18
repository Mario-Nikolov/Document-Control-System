import {Routes,Route, Navigate} from 'react-router-dom'
import Login from './components/login/Login'
import Home from './components/home/home'
import Profile from './components/profile/Profile'
import DocDetails from './components/documentDetails/DocDetails'
import { useAuthContext } from './context/AuthContext'
import UserDetailsModal from './components/userDetails/UserDetails'


function App() {
  const {isAuthenticated} = useAuthContext();

  return (
      <main>
        <Routes>
          <Route path='/login' element={<Login/>}/>
          <Route path='/' element={isAuthenticated ?<Home/> : <Navigate to='/login'/>}/>
          <Route path='/profile' element={<Profile/>}/>
          <Route path='/documents/:documentId' element={<DocDetails/>}/>
        </Routes>
      </main>
  )
}

export default App
