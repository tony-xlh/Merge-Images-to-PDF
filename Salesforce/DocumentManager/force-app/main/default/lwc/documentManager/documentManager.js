import { LightningElement } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { loadScript, loadStyle } from 'lightning/platformResourceLoader';
import ddv from '@salesforce/resourceUrl/ddv';

export default class DocumentManager extends LightningElement {
  ddvInitialized = false;
  ddvFrame;
  dataURL = "";

  get dataURLLabel() {
    if (this.dataURL.length > 15) {
      return this.dataURL.substring(0,10) + "...";
    }else{
      return this.dataURL;
    }
  }
  
  renderedCallback() {
      if (this.ddvInitialized) {
          return;
      }
      this.ddvInitialized = true;
      window.addEventListener(
        "message",
        (event) => {
          console.log(event);
          console.log("received message in lwc");
          this.dataURL = event.data;
        },
        false,
      );
      this.ddvFrame = document.createElement('iframe');
      this.ddvFrame.src = ddv + "/index.html";
      // div tag in which iframe will be added should have id attribute with value myDIV
      this.template.querySelector("div.ddvViewer").appendChild(this.ddvFrame);

      // provide height and width to it
      this.ddvFrame.setAttribute("style","height:100%;width:100%;");
  }

  merge(){
    this.ddvFrame.contentWindow.postMessage("merge");
  }
}