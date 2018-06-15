import React, { Component } from 'react';
import './App.css';
import { Tab, Tabs } from "@material-ui/core/es/index";
import { Users } from "./Users";
import { Events } from "./Events";

class App extends Component {

  state = {
    value: 0,
  };

  handleChange = (event, value) => {
    this.setState({ value });
  };

  render() {
    return (
      <div className="App">
        <Tabs value={this.state.value} onChange={this.handleChange}>
          <Tab label={"Users"}/>
          <Tab label={"Items"}/>
          <Tab label={"Orders"}/>
          <Tab label={"Events"}/>
        </Tabs>
        {this.state.value === 0 && <Users />}
        {this.state.value === 3 && <Events />}
      </div>
    );
  }
}

export default App;
