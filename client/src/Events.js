import React, {Component} from "react";
import axios from "axios";
import { TextField } from "@material-ui/core/es/index";

export class Events extends Component {

  constructor(props) {
    super(props);
    this.fetchEvents();
    this.state = {
      events: [],
      filter: "",
    };
  }

  componentWillReceiveProps = (props) => {
    this.fetchUsers();
  };

  fetchEvents = () => {
    fetch("http://localhost:8000/events/")
      .then(response => response.json())
      .then(events => this.setState({events}));
  };

  getPrettyPrint = (event) => {
    const str = JSON.stringify(event, null, 2);
    return str.slice(1, str.length - 1);
  };

  getEvents = () => {
    return this.state.events.filter(event => {
      const str = this.getPrettyPrint(event);
      return str.includes(this.state.filter);
    }).sort((a, b) => b.createdAt.localeCompare(a.createdAt));
  };

  render() {
    return (
      <div>
        <TextField id={"name"}
                   label={"name"}
                   onChange={(e) => this.setState({filter: e.target.value})}
                   onKeyDown={(event) => {
                     if(event.keyCode === 13) {
                       // this.createUser(event.target.value);
                       // event.target.value = "";
                     }
                   }}
        />
        {this.getEvents().map(event => {
          return (
            <div key={event.eventId} style={{border: "1px solid black", margin: "2px"}}>
              <pre>{this.getPrettyPrint(event)}</pre>
            </div>
          )
        })}
      </div>
    )
  }
}