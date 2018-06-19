import React, { Component } from 'react';
import './App.css';
import axios from "axios";
import { Tab, Tabs } from "@material-ui/core/es/index";
import { Users } from "./Users";
import { Events } from "./Events";
import { Items } from "./Items";

class App extends Component {

  state = {
    value: 0,
  };

  handleChange = (event, value) => {
    this.setState({ value });
  };

  onGenerate = () => {
    console.log("GENERATING")
    console.log(this.refs.generate);
    this.refs.generate.textContent = "GENERATING";

    const promises = [];
    for (let i = 0; i < 100; i++) {
      promises.push(() => axios.post("http://localhost:8001/user/" + Math.random()))
    }

    const t0 = performance.now();

    Promise.all(promises.map(fn => fn()))
      .then(() => this.refs.generate.textContent = "generate")
      .then(() => fetch("http://localhost:8001/user/all"))
      .then(response => response.json())
      .then(users => {
        const ids = users.map(user => user.id);
        return Promise.all(
          ids.map(id => {
            return Promise.all([
              axios.delete("http://localhost:8001/user/" + id),
              axios.patch("http://localhost:8001/user/" + id + "?name=" + Math.random())
            ]);
          })
        );
      })
      .then(() => console.log(performance.now() - t0));
  }

  render() {
    return (
      <div className="App">
        <button onClick={this.onGenerate} ref={"generate"}>generate</button>
        <Tabs value={this.state.value} onChange={this.handleChange}>
          <Tab label={"Users"}/>
          <Tab label={"Items"}/>
          <Tab label={"Orders"}/>
          <Tab label={"Events"}/>
        </Tabs>
        {this.state.value === 0 && <Users/>}
        {this.state.value === 1 && <Items/>}
        {this.state.value === 3 && <Events/>}
      </div>
    );
  }
}

export default App;
