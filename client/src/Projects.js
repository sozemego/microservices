import React, {Component} from "react";
import axios from "axios";
import { Button, Chip, Select, TextField } from "@material-ui/core/es/index";
import MenuItem from '@material-ui/core/MenuItem';

export class Projects extends Component {

  constructor(props) {
    super(props);
    this.fetchAll();
    this.state = {
      projects: [],
      users: [],
    };
  }

  componentWillReceiveProps = (props) => {
    this.fetchAll();
  };

  fetchProjects = () => {
    fetch("http://localhost:8002/project/all")
      .then(response => response.json())
      .then(projects => this.setState({projects}));
  };

  fetchUsers = () => {
    fetch("http://localhost:8001/user/all")
      .then(response => response.json())
      .then(users => this.setState({users}));
  };

  userItem = (userId, projectId) => {
    const {users} = this.state;
    const user = users.find(user => user.id === userId);
    if(!user) return null;
    return (
      <Chip key={user.id} label={user.name} onDelete={() => this.removeUserFromProject(projectId, userId)}/>
    );
  };

  getUsers = (projectId) => {
    const project = this.state.projects.find(p => p.id === projectId);

    return this.state.users.filter(user => {
      return !project.userIds.find(userId => userId === user.id);
    }).map(user => {
      return (
        <MenuItem key={user.id} value={user.id}>{user.name}</MenuItem>
      )
    });
  };

  getProjectComponent = (project) => {
    console.log(project);
    return (
      <div style={{display: "flex", flexDirection: "row", justifyContent: "start", alignItems: "center"}}>
        <TextField defaultValue={project.name}
                   label={"name"}
                   onKeyDown={(event) => {
                     if(event.keyCode === 13) {
                       this.changeProjectName(project.id, event.target.value);
                     }
                   }}
                   style={{margin: "4px"}}
        />
        <TextField defaultValue={this.getDateDefaultValue(project.startDate)}
                   type={"date"}
                   onChange={e => this.changeProjectStartDate(project.id, new Date(e.target.value))}
                   style={{margin: "4px"}}
        />
        <TextField defaultValue={this.getDateDefaultValue(project.endDate)}
                   type={"date"}
                   onChange={e => this.changeProjectEndDate(project.id, new Date(e.target.value))}
                   style={{margin: "4px"}}
        />
        <Select value={''}
                onChange={(e) => this.assignUserToProject(project.id, e.target.value)}
        >
          {this.getUsers(project.id)}
        </Select>
        {project.userIds.map(id => this.userItem(id, project.id))}
        <div style={{cursor: "pointer", margin: "4px"}} onClick={() => this.deleteProject(project.id)}>DELETE</div>
      </div>
    );
  };

  getDateDefaultValue = (dateStr) => {
    const date = new Date(dateStr);
    return date.toISOString().substr(0, 10);
  };

  deleteProject = (id) => {
    axios.delete("http://localhost:8002/project/" + id)
      .then(this.fetchAll);
  };

  addProject = (name) => {
    axios.post("http://localhost:8002/project/create/" + encodeURIComponent(name))
      .then(this.fetchAll);
  };

  changeProjectName = (id, name) => {
    axios.patch("http://localhost:8002/project/name/" + id + "?name=" + encodeURIComponent(name))
      .then(this.fetchAll);
  };

  changeProjectStartDate = (id, date) => {
    axios.patch("http://localhost:8002/project/startdate/" + id + "?startdate=" + date.toISOString())
      .then(this.fetchAll);
  };

  changeProjectEndDate = (id, date) => {
    axios.patch("http://localhost:8002/project/enddate/" + id + "?enddate=" + date.toISOString())
      .then(this.fetchAll);
  };

  assignUserToProject = (projectId, userId) => {
    axios.post("http://localhost:8002/project/assign/" + projectId + "?userId=" + userId)
      .then(this.fetchAll)
  };

  removeUserFromProject = (projectId, userId) => {
    axios.post("http://localhost:8002/project/remove/" + projectId + "?userId=" + userId)
      .then(this.fetchAll)
  };

  fetchAll = () => {
    this.fetchProjects();
    this.fetchUsers();
  };

  render() {
    return (
      <div>
        <TextField id={"name"}
                   label={"name"}
                   onKeyDown={(event) => {
                     if(event.keyCode === 13) {
                       this.addProject(event.target.value);
                       event.target.value = "";
                     }
                   }}
        />
        {this.state.projects.map(project => {
          return (
            <div key={project.id} style={{border: "1px solid black", margin: "2px"}}>
              {this.getProjectComponent(project)}
            </div>
          );
        })}
      </div>
    )
  }
}