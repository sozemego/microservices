import React, {Component} from "react";
import axios from "axios";
import { TextField } from "@material-ui/core/es/index";

export class Users extends Component {

  constructor(props) {
    super(props);
    this.fetchUsers();
    this.state = {
      users: [],
    };
  }

  componentWillReceiveProps = (props) => {
    this.fetchUsers();
  };

  fetchUsers = () => {
    fetch("http://localhost:8001/user/all")
      .then(response => response.json())
      .then(users => this.setState({users}));
  };

  deleteUser = (id) => {
    return axios.delete("http://localhost:8001/user/" + id)
      .then(this.fetchUsers);
  };

  createUser = (name) => {
    return axios.post("http://localhost:8001/user/" + name)
      .then(this.fetchUsers);
  };

  changeUserName = (id, name) => {
    return axios.patch("http://localhost:8001/user/" + id + "?name=" + name);
  };

  render() {
    const {users} = this.state;
    return (
      <div>
        <TextField id={"name"}
                   label={"name"}
                   onKeyDown={(event) => {
                     if(event.keyCode === 13) {
                       this.createUser(event.target.value);
                       event.target.value = "";
                     }
                   }}
        />
        {users.map(user => {
          return (
            <div key={user.id} style={{padding: "12px", display: "flex", flexDirection: "row"}}>
              <TextField defaultValue={user.name}
                         onKeyDown={(event) => {
                           if(event.keyCode === 13) {
                               this.changeUserName(user.id, event.target.value);
                           }
                         }}
              />
              <div style={{paddingLeft: "4px", cursor: "pointer"}} onClick={() => {
                this.deleteUser(user.id)
                this.changeUserName(user.id, "NEW NAME");
              }}>DELETE</div>
            </div>
          )
        })}
      </div>
    )
  }
}