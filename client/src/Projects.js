import React, {Component} from "react";
import axios from "axios";
import { Button, TextField } from "@material-ui/core/es/index";

export class Projects extends Component {

  constructor(props) {
    super(props);
    this.fetchProjects();
    this.state = {
      projects: [],
    };
  }

  componentWillReceiveProps = (props) => {
    this.fetchUsers();
  };

  fetchProjects = () => {
    fetch("http://localhost:8002/project/all")
      .then(response => response.json())
      .then(projects => this.setState({projects}));
  };

  getProjectComponent = (project) => {
    console.log(project);
    return (
      <div style={{display: "flex", flexDirection: "row", justifyContent: "space-between"}}>
        <div>{project.name}</div>
        <div>{project.startDate}</div>
        <div>{project.endDate}</div>
        <div style={{cursor: "pointer"}} onClick={() => this.deleteProject(project.id)}>DELETE</div>
      </div>
    );
  };

  deleteProject = (id) => {
    axios.delete("http://localhost:8002/project/" + id)
      .then(this.fetchProjects);
  };

  addProject = (name) => {
    axios.post("http://localhost:8002/project/create/" + name)
      .then(this.fetchProjects);
  };

  render() {
    return (
      <div>
        <TextField id={"name"}
                   label={"name"}
                   onChange={(e) => this.setState({filter: e.target.value})}
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
          )
        })}
      </div>
    )
  }
}