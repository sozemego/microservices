import React, { Component } from 'react';
import './App.css';
import axios from "axios";
import { Tab, Tabs } from "@material-ui/core/es/index";
import { Users } from "./Users";
import { Events } from "./Events";
import { Projects } from "./Projects";

class App extends Component {

  state = {
    value: 1,
  };

  handleChange = (event, value) => {
    this.setState({ value });
  };

  render() {
    return (
      <div className="App">
        <Tabs value={this.state.value} onChange={this.handleChange}>
          <Tab label={"Users"}/>
          <Tab label={"Projects"}/>
          <Tab label={"Events"}/>
        </Tabs>
        {this.state.value === 0 && <Users/>}
        {this.state.value === 1 && <Projects/>}
        {this.state.value === 2 && <Events/>}
      </div>
    );
  }
}

export default App;
