import React, {Component} from "react";
import axios from "axios";
import { TextField } from "@material-ui/core/es/index";

export class Events extends Component {

  constructor(props) {
    super(props);
    this.fetchEvents();
    this.state = {
      events: [],
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

  render() {
    const {events} = this.state;
    return (
      <div>
        <TextField id={"name"}
                   label={"name"}
                   onKeyDown={(event) => {
                     if(event.keyCode === 13) {
                       // this.createUser(event.target.value);
                       // event.target.value = "";
                     }
                   }}
        />
        {events.map(event => {
          return (
            <div key={event.eventId} style={{border: "1px solid black", margin: "2px"}}>
              <pre>{JSON.stringify(event, null, 2)}</pre>
            </div>
          )
        })}
      </div>
    )
  }
}