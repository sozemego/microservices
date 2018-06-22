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
    this.fetchProjects();
  };

  fetchProjects = () => {
    fetch("http://localhost:8002/project/all")
      .then(response => response.json())
      .then(projects => this.setState({projects}));
  };

  getProjectComponent = (project) => {
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
      .then(this.fetchProjects);
  };

  addProject = (name) => {
    axios.post("http://localhost:8002/project/create/" + encodeURIComponent(name))
      .then(this.fetchProjects);
  };

  changeProjectName = (id, name) => {
    axios.patch("http://localhost:8002/project/name/" + id + "?name=" + encodeURIComponent(name))
      .then(this.fetchProjects);
  };

  changeProjectStartDate = (id, date) => {
    axios.patch("http://localhost:8002/project/startdate/" + id + "?startdate=" + date.toISOString())
      .then(this.fetchProjects);
  };

  changeProjectEndDate = (id, date) => {
    axios.patch("http://localhost:8002/project/enddate/" + id + "?enddate=" + date.toISOString())
      .then(this.fetchProjects);
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
          )
        })}
      </div>
    )
  }
}